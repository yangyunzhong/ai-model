package org.dromara.model.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.oss.factory.OssFactory;
import org.dromara.common.tenant.helper.TenantHelper;
import org.dromara.model.domain.ModelDefinition;
import org.dromara.model.domain.ModelScript;
import org.dromara.model.mapper.ModelDefinitionMapper;
import org.dromara.model.mapper.ModelScriptMapper;
import org.dromara.model.runtime.ModelContainerManager;
import org.dromara.model.runtime.RuntimeProperties;
import org.dromara.model.runtime.RuntimeScriptStore;
import org.dromara.model.service.IModelRuntimeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 模型运行时Service业务层处理
 *
 * <p>容器形态：一个「已发布」模型对应一个常驻沙箱容器（{@code sleep infinity} 保活），
 * 脚本通过 bind mount 挂载进容器，运行时以 {@code docker exec} 驱动。</p>
 *
 * @author model-platform
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ModelRuntimeServiceImpl implements IModelRuntimeService {

    /**
     * 模型状态
     */
    private static final String STATUS_PUBLISHED = "已发布";
    private static final String STATUS_OFFLINE = "已下架";

    /**
     * 容器状态
     */
    private static final String CONTAINER_RUNNING = "running";
    private static final String CONTAINER_MISSING = "missing";
    private static final String CONTAINER_DISABLED = "disabled";

    /**
     * 下架时停止容器的超时时间（秒）
     */
    private static final int OFFLINE_STOP_TIMEOUT = 10;

    /**
     * 依赖安装失败时保留的 stderr 最大长度
     */
    private static final int MAX_ERROR_LENGTH = 2000;

    private final ModelDefinitionMapper definitionMapper;
    private final ModelScriptMapper scriptMapper;
    private final RuntimeProperties properties;
    private final RuntimeScriptStore scriptStore;
    private final ModelContainerManager containerManager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(String modelId) {
        ModelDefinition definition = definitionMapper.selectById(modelId);
        if (definition == null) {
            throw new ServiceException("模型不存在：" + modelId);
        }
        ModelScript script = selectCurrentScript(modelId);
        if (script == null) {
            throw new ServiceException("该模型尚未上传算法脚本，无法发布");
        }

        boolean runtimeEnabled = Boolean.TRUE.equals(properties.getEnabled());
        boolean hasRequirements = StringUtils.isNotBlank(script.getRequirementsOssId());
        String containerId = null;
        try {
            // 1) 物化脚本（必要时含 requirements.txt）到宿主机脚本目录，供 bind mount 使用
            Path localScript = scriptStore.materializeScript(modelId, script.getFileName(), script.getOriginalName());
            String containerScriptPath = scriptStore.containerScriptPath(modelId, localScript.getFileName().toString());
            if (hasRequirements) {
                scriptStore.materializeRequirements(modelId, script.getRequirementsOssId());
            }

            // 2) 运行时启用时创建并启动容器，随后安装依赖
            if (runtimeEnabled) {
                // bind 源为脚本根目录，容器内路径约定为 <containerScriptDir>/<modelId>/<fileName>
                containerId = containerManager.createAndStartContainer(modelId, properties.getScriptDir());
                if (hasRequirements) {
                    ModelContainerManager.ExecResult installResult = containerManager.installRequirements(
                        containerId, scriptStore.containerRequirementsPath(modelId),
                        properties.getInstallTimeoutSeconds());
                    if (!installResult.success()) {
                        throw new ServiceException("依赖安装失败：" + truncate(installResult.stderr()));
                    }
                }
            }

            // 3) 回写模型定义状态与运行时字段
            var update = Wrappers.<ModelDefinition>lambdaUpdate()
                .eq(ModelDefinition::getId, modelId)
                .set(ModelDefinition::getStatus, STATUS_PUBLISHED)
                .set(ModelDefinition::getScriptPath, containerScriptPath)
                .set(ModelDefinition::getLastPublishTime, new Date());
            if (containerId != null) {
                update.set(ModelDefinition::getContainerId, containerId)
                    .set(ModelDefinition::getContainerName, containerManager.containerName(modelId))
                    .set(ModelDefinition::getContainerStatus, CONTAINER_RUNNING)
                    .set(ModelDefinition::getRuntimeImage, properties.getImage());
            } else {
                update.set(ModelDefinition::getContainerStatus, CONTAINER_DISABLED);
            }
            definitionMapper.update(null, update);
            log.info("模型发布成功, modelId => {}, containerId => {}, runtimeEnabled => {}",
                modelId, containerId, runtimeEnabled);
        } catch (RuntimeException e) {
            // 回滚已产生的容器与脚本目录，避免残留（按名称删除可覆盖「已创建未启动」的场景）
            cleanupOnFailure(modelId, runtimeEnabled);
            log.error("模型发布失败, modelId => {}", modelId, e);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void offline(String modelId) {
        ModelDefinition definition = definitionMapper.selectById(modelId);
        if (definition == null) {
            throw new ServiceException("模型不存在：" + modelId);
        }
        if (Boolean.TRUE.equals(properties.getEnabled()) && StringUtils.isNotBlank(definition.getContainerId())) {
            containerManager.stopContainer(definition.getContainerId(), OFFLINE_STOP_TIMEOUT);
            containerManager.removeContainer(definition.getContainerId(), true);
        }
        scriptStore.deleteModelDir(modelId);

        definitionMapper.update(null, Wrappers.<ModelDefinition>lambdaUpdate()
            .eq(ModelDefinition::getId, modelId)
            .set(ModelDefinition::getStatus, STATUS_OFFLINE)
            .set(ModelDefinition::getContainerId, null)
            .set(ModelDefinition::getContainerName, null)
            .set(ModelDefinition::getContainerStatus, null));
        log.info("模型下架成功, modelId => {}", modelId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void destroy(String modelId) {
        ModelDefinition definition = definitionMapper.selectById(modelId);
        String containerId = definition == null ? null : definition.getContainerId();

        // 1) 停止并删除沙箱容器（按 ID 或名称删除，覆盖「未发布/已创建未启动」场景）
        if (Boolean.TRUE.equals(properties.getEnabled())) {
            try {
                if (StringUtils.isNotBlank(containerId)) {
                    containerManager.stopContainer(containerId, OFFLINE_STOP_TIMEOUT);
                    containerManager.removeContainer(containerId, true);
                } else {
                    containerManager.removeContainer(containerManager.containerName(modelId), true);
                }
            } catch (Exception e) {
                log.warn("删除模型时清理容器异常, modelId => {}", modelId, e);
            }
        }

        // 2) 清理宿主机脚本目录
        scriptStore.deleteModelDir(modelId);

        // 3) 删除 MinIO 脚本对象并清理 model_script 记录
        List<ModelScript> scripts = scriptMapper.selectList(Wrappers.<ModelScript>lambdaQuery()
            .eq(ModelScript::getModelId, modelId));
        for (ModelScript script : scripts) {
            deleteOssObject(script.getFileName());
            deleteOssObject(script.getRequirementsOssId());
            scriptMapper.deleteById(script.getId());
        }
        log.info("模型运行时物料已清理, modelId => {}, scriptCount => {}", modelId, scripts.size());
    }

    /**
     * 删除 MinIO 中的对象，失败仅告警不阻断删除流程
     *
     * @param objectKey 对象键（model_script.file_name / requirements_oss_id）
     */
    private void deleteOssObject(String objectKey) {
        if (StringUtils.isBlank(objectKey)) {
            return;
        }
        try {
            OssFactory.instance().delete(objectKey);
            log.info("MinIO 脚本对象已删除, objectKey => {}", objectKey);
        } catch (Exception e) {
            log.warn("删除 MinIO 脚本对象失败, objectKey => {}", objectKey, e);
        }
    }

    @Override
    public void reconcileOnStartup() {
        if (!Boolean.TRUE.equals(properties.getEnabled())) {
            log.info("模型运行时未启用（model.runtime.enabled=false），跳过容器对账");
            return;
        }
        try {
            // 启动阶段无租户上下文，需忽略租户过滤以全量对账
            TenantHelper.ignore(this::doReconcile);
        } catch (Exception e) {
            log.warn("模型容器启动对账失败，跳过（不影响应用启动）", e);
        }
    }

    /**
     * 启动对账：同步已发布模型的容器状态，并清理孤儿容器
     */
    private void doReconcile() {
        List<ModelContainerManager.ManagedContainer> managed = containerManager.listManagedContainers();
        Map<String, ModelContainerManager.ManagedContainer> byModelId = new HashMap<>(managed.size());
        for (ModelContainerManager.ManagedContainer container : managed) {
            if (StringUtils.isNotBlank(container.modelId())) {
                byModelId.put(container.modelId(), container);
            }
        }

        List<ModelDefinition> published = definitionMapper.selectList(Wrappers.<ModelDefinition>lambdaQuery()
            .eq(ModelDefinition::getStatus, STATUS_PUBLISHED));
        Set<String> publishedIds = new HashSet<>(published.size());
        for (ModelDefinition definition : published) {
            String modelId = definition.getId();
            publishedIds.add(modelId);
            ModelContainerManager.ManagedContainer container = byModelId.get(modelId);
            if (container == null) {
                log.warn("已发布模型缺少运行容器, modelId => {}, containerId => {}",
                    modelId, definition.getContainerId());
                definitionMapper.update(null, Wrappers.<ModelDefinition>lambdaUpdate()
                    .eq(ModelDefinition::getId, modelId)
                    .set(ModelDefinition::getContainerStatus, CONTAINER_MISSING)
                    .set(ModelDefinition::getContainerId, null)
                    .set(ModelDefinition::getContainerName, null));
            } else {
                definitionMapper.update(null, Wrappers.<ModelDefinition>lambdaUpdate()
                    .eq(ModelDefinition::getId, modelId)
                    .set(ModelDefinition::getContainerId, container.id())
                    .set(ModelDefinition::getContainerName, container.name())
                    .set(ModelDefinition::getContainerStatus, CONTAINER_RUNNING));
            }
        }

        // 清理不属于任何已发布模型的孤儿容器
        for (ModelContainerManager.ManagedContainer container : managed) {
            String modelId = container.modelId();
            if (StringUtils.isBlank(modelId) || !publishedIds.contains(modelId)) {
                log.warn("清理孤儿模型容器, modelId => {}, containerId => {}", modelId, container.id());
                containerManager.removeContainer(container.id(), true);
            }
        }
        log.info("模型容器启动对账完成, 托管容器数 => {}, 已发布模型数 => {}", managed.size(), publishedIds.size());
    }

    /**
     * 取模型的当前生效脚本版本
     */
    private ModelScript selectCurrentScript(String modelId) {
        List<ModelScript> scripts = scriptMapper.selectList(Wrappers.<ModelScript>lambdaQuery()
            .eq(ModelScript::getModelId, modelId)
            .eq(ModelScript::getIsCurrent, "1")
            .orderByDesc(ModelScript::getCreateTime));
        return scripts.isEmpty() ? null : scripts.get(0);
    }

    /**
     * 发布失败时清理容器与脚本目录，避免残留
     */
    private void cleanupOnFailure(String modelId, boolean runtimeEnabled) {
        if (runtimeEnabled) {
            try {
                containerManager.removeContainer(containerManager.containerName(modelId), true);
            } catch (Exception e) {
                log.warn("发布失败后清理容器异常, modelId => {}", modelId, e);
            }
        }
        scriptStore.deleteModelDir(modelId);
    }

    /**
     * 截断过长的错误信息
     */
    private String truncate(String text) {
        if (StringUtils.isBlank(text)) {
            return "未知错误";
        }
        return text.length() <= MAX_ERROR_LENGTH ? text : text.substring(0, MAX_ERROR_LENGTH);
    }

}
