package org.dromara.model.runtime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.oss.core.OssClient;
import org.dromara.common.oss.factory.OssFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * 运行时脚本物料仓库
 *
 * <p>发布模型时把 OSS（MinIO）中的脚本与 requirements.txt 物化到宿主机脚本目录，
 * 该目录通过 bind mount 挂载进容器 {@code /app/scripts}，从而实现「脚本不进镜像、
 * 改脚本即改挂载目录」的运行方式。</p>
 *
 * <p>目录约定：{@code <scriptDir>/<modelId>/<fileName>}，
 * 容器内路径为 {@code <containerScriptDir>/<modelId>/<fileName>}。</p>
 *
 * @author model-platform
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RuntimeScriptStore {

    private static final String REQUIREMENTS_FILE = "requirements.txt";
    private static final String PY_SUFFIX = ".py";

    private final RuntimeProperties properties;

    /**
     * 宿主机模型脚本目录
     */
    public String hostModelDir(String modelId) {
        return properties.getScriptDir() + "/" + modelId;
    }

    /**
     * 容器内模型脚本目录
     */
    public String containerModelDir(String modelId) {
        return properties.getContainerScriptDir() + "/" + modelId;
    }

    /**
     * 容器内脚本文件绝对路径
     *
     * @param modelId       模型ID
     * @param localFileName 宿主机上的脚本文件名
     */
    public String containerScriptPath(String modelId, String localFileName) {
        return containerModelDir(modelId) + "/" + localFileName;
    }

    /**
     * 容器内 requirements.txt 绝对路径
     */
    public String containerRequirementsPath(String modelId) {
        return containerModelDir(modelId) + "/" + REQUIREMENTS_FILE;
    }

    /**
     * 物化脚本文件：从 OSS 下载并落到宿主机脚本目录，返回宿主机本地文件路径
     *
     * @param modelId       模型ID
     * @param objectKey     脚本在 OSS 中的对象键（model_script.file_name）
     * @param preferredName 期望的本地文件名（一般取原始文件名，需以 .py 结尾）
     * @return 宿主机本地文件路径
     */
    public Path materializeScript(String modelId, String objectKey, String preferredName) {
        String fileName = resolveFileName(objectKey, preferredName);
        Path target = Paths.get(hostModelDir(modelId), fileName);
        downloadTo(objectKey, target);
        log.info("脚本已物化, modelId => {}, objectKey => {}, target => {}", modelId, objectKey, target);
        return target;
    }

    /**
     * 物化 requirements.txt：从 OSS 下载并落到宿主机脚本目录，返回宿主机本地文件路径
     *
     * @param modelId   模型ID
     * @param objectKey requirements.txt 在 OSS 中的对象键
     * @return 宿主机本地文件路径
     */
    public Path materializeRequirements(String modelId, String objectKey) {
        Path target = Paths.get(hostModelDir(modelId), REQUIREMENTS_FILE);
        downloadTo(objectKey, target);
        log.info("依赖清单已物化, modelId => {}, objectKey => {}, target => {}", modelId, objectKey, target);
        return target;
    }

    /**
     * 删除模型脚本目录（下架时清理），幂等
     */
    public void deleteModelDir(String modelId) {
        Path dir = Paths.get(hostModelDir(modelId));
        if (!Files.exists(dir)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(dir)) {
            walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    log.warn("删除脚本文件失败: {}", p, e);
                }
            });
            log.info("模型脚本目录已清理, modelId => {}, dir => {}", modelId, dir);
        } catch (IOException e) {
            log.warn("清理模型脚本目录失败, modelId => {}, dir => {}", modelId, dir, e);
        }
    }

    /**
     * 解析本地文件名：优先使用期望名（需以 .py 结尾），否则回退到对象键的 basename
     */
    private String resolveFileName(String objectKey, String preferredName) {
        if (StringUtils.isNotBlank(preferredName) && StringUtils.endsWithIgnoreCase(preferredName, PY_SUFFIX)) {
            return preferredName;
        }
        if (StringUtils.isNotBlank(objectKey)) {
            int idx = objectKey.lastIndexOf('/');
            String base = idx >= 0 ? objectKey.substring(idx + 1) : objectKey;
            if (StringUtils.isNotBlank(base)) {
                return base;
            }
        }
        throw new ServiceException("无法确定脚本文件名");
    }

    /**
     * 从 OSS 下载对象到本地目标路径
     */
    private void downloadTo(String objectKey, Path target) {
        if (StringUtils.isBlank(objectKey)) {
            throw new ServiceException("OSS 对象键为空，无法下载文件");
        }
        try {
            Files.createDirectories(target.getParent());
            OssClient storage = OssFactory.instance();
            Path temp = storage.fileDownload(objectKey);
            try {
                Files.copy(temp, target, StandardCopyOption.REPLACE_EXISTING);
            } finally {
                Files.deleteIfExists(temp);
            }
        } catch (IOException e) {
            throw new ServiceException("物化文件失败: " + e.getMessage());
        }
    }

}
