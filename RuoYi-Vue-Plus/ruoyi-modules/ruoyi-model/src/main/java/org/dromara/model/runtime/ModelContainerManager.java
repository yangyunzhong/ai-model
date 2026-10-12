package org.dromara.model.runtime;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.InspectExecResponse;
import com.github.dockerjava.api.exception.ConflictException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.AccessMode;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.StreamType;
import com.github.dockerjava.api.model.Volume;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 模型沙箱容器管理器
 *
 * <p>一个「已发布」模型对应一个常驻容器：容器以 {@code sleep infinity} 保活，
 * 通过 {@code docker exec} 驱动脚本执行。发布时创建并启动容器、安装依赖，
 * 下架时停止并删除容器。</p>
 *
 * @author model-platform
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ModelContainerManager {

    /**
     * 平台托管容器统一标签，便于按标签筛选/清理
     */
    public static final String LABEL_APP = "app";
    public static final String LABEL_APP_VALUE = "model-platform";
    public static final String LABEL_MODEL_ID = "model-id";

    /**
     * 容器名前缀
     */
    private static final String CONTAINER_PREFIX = "model-";

    /**
     * 容器保活命令
     */
    private static final String[] KEEP_ALIVE_CMD = {"sleep", "infinity"};

    private final DockerClientProvider clientProvider;
    private final RuntimeProperties properties;

    /**
     * 依据模型ID生成容器名
     */
    public String containerName(String modelId) {
        return CONTAINER_PREFIX + modelId;
    }

    /**
     * 创建并启动模型容器（幂等：同名容器已存在时先强制删除再重建）
     *
     * @param modelId       模型ID
     * @param hostScriptDir 宿主机脚本目录（bind mount 源）
     * @return 容器ID
     */
    public String createAndStartContainer(String modelId, String hostScriptDir) {
        DockerClient client = clientProvider.getClient();
        String name = containerName(modelId);
        // 清理同名残留容器，保证幂等
        removeByName(client, name);
        HostConfig hostConfig = HostConfig.newHostConfig()
            .withMemory(properties.getMemoryMb() * 1024 * 1024)
            .withNanoCPUs((long) (properties.getCpus() * 1_000_000_000L))
            .withPidsLimit(properties.getPidsLimit())
            .withNetworkMode(properties.getNetwork())
            .withBinds(new Bind(hostScriptDir, new Volume(properties.getContainerScriptDir()), AccessMode.rw))
            .withAutoRemove(false);
        Map<String, String> labels = new HashMap<>(4);
        labels.put(LABEL_APP, LABEL_APP_VALUE);
        labels.put(LABEL_MODEL_ID, modelId);
        List<String> env = new ArrayList<>(5);
        env.add("MODEL_DB_HOST=" + properties.getDbHost());
        env.add("MODEL_DB_PORT=" + properties.getDbPort());
        env.add("MODEL_DB_NAME=" + properties.getDbName());
        env.add("MODEL_DB_USER=" + properties.getDbUser());
        env.add("MODEL_DB_PASSWORD=" + properties.getDbPassword());
        CreateContainerResponse response = client.createContainerCmd(properties.getImage())
            .withName(name)
            .withLabels(labels)
            .withEnv(env)
            .withHostConfig(hostConfig)
            .withCmd(KEEP_ALIVE_CMD)
            .withTty(false)
            .withAttachStdout(false)
            .withAttachStderr(false)
            .exec();
        String containerId = response.getId();
        client.startContainerCmd(containerId).exec();
        log.info("模型容器已创建并启动, modelId => {}, containerId => {}, name => {}", modelId, containerId, name);
        return containerId;
    }

    /**
     * 启动容器（幂等：已运行则忽略）
     */
    public void startContainer(String containerId) {
        try {
            clientProvider.getClient().startContainerCmd(containerId).exec();
        } catch (NotFoundException e) {
            throw new IllegalStateException("容器不存在: " + containerId, e);
        } catch (NotModifiedException e) {
            log.debug("容器已在运行，忽略启动: {}", containerId);
        }
    }

    /**
     * 停止容器（幂等：不存在或已停止均忽略）
     */
    public void stopContainer(String containerId, int timeoutSeconds) {
        try {
            clientProvider.getClient().stopContainerCmd(containerId).withTimeout(timeoutSeconds).exec();
            log.info("容器已停止, containerId => {}", containerId);
        } catch (NotFoundException e) {
            log.debug("容器不存在，忽略停止: {}", containerId);
        } catch (NotModifiedException e) {
            log.debug("容器已处于停止状态: {}", containerId);
        }
    }

    /**
     * 删除容器（幂等：不存在则忽略）
     */
    public void removeContainer(String containerId, boolean force) {
        try {
            clientProvider.getClient().removeContainerCmd(containerId)
                .withForce(force)
                .withRemoveVolumes(false)
                .exec();
            log.info("容器已删除, containerId => {}", containerId);
        } catch (NotFoundException e) {
            log.debug("容器不存在，忽略删除: {}", containerId);
        }
    }

    /**
     * 判断容器是否存在
     */
    public boolean exists(String containerId) {
        if (containerId == null || containerId.isBlank()) {
            return false;
        }
        try {
            clientProvider.getClient().inspectContainerCmd(containerId).exec();
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    /**
     * 查询容器状态（running/exited/created 等），容器不存在返回 null
     */
    public String inspectStatus(String containerId) {
        if (containerId == null || containerId.isBlank()) {
            return null;
        }
        try {
            InspectContainerResponse response = clientProvider.getClient().inspectContainerCmd(containerId).exec();
            return response.getState().getStatus();
        } catch (NotFoundException e) {
            return null;
        }
    }

    /**
     * 在容器内执行命令
     *
     * @param containerId    容器ID
     * @param cmd            命令及参数
     * @param timeoutSeconds 超时时间（秒）
     * @return 执行结果
     */
    public ExecResult exec(String containerId, String[] cmd, int timeoutSeconds) {
        DockerClient client = clientProvider.getClient();
        ExecCreateCmdResponse execCreate = client.execCreateCmd(containerId)
            .withAttachStdout(true)
            .withAttachStderr(true)
            .withTty(false)
            .withCmd(cmd)
            .exec();
        String execId = execCreate.getId();
        StringBuilder stdout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();
        ResultCallback.Adapter<Frame> callback = new ResultCallback.Adapter<>() {
            @Override
            public void onNext(Frame frame) {
                if (frame == null || frame.getPayload() == null) {
                    return;
                }
                String text = new String(frame.getPayload(), StandardCharsets.UTF_8);
                if (frame.getStreamType() == StreamType.STDERR) {
                    stderr.append(text);
                } else {
                    stdout.append(text);
                }
            }
        };
        boolean completed;
        try {
            completed = client.execStartCmd(execId)
                .exec(callback)
                .awaitCompletion(timeoutSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            closeQuietly(callback);
            return new ExecResult(stdout.toString(), stderr.toString(), -1, true);
        } catch (RuntimeException e) {
            closeQuietly(callback);
            throw e;
        }
        closeQuietly(callback);
        if (!completed) {
            log.warn("容器命令执行超时, containerId => {}, cmd => {}", containerId, String.join(" ", cmd));
            return new ExecResult(stdout.toString(), stderr.toString(), -1, true);
        }
        InspectExecResponse inspect = client.inspectExecCmd(execId).exec();
        Long exitCode = inspect.getExitCodeLong();
        int code = exitCode == null ? -1 : exitCode.intValue();
        return new ExecResult(stdout.toString(), stderr.toString(), code, false);
    }

    /**
     * 安装依赖（requirements.txt）
     *
     * @param containerId             容器ID
     * @param requirementsContainerPath 容器内 requirements.txt 绝对路径
     * @param timeoutSeconds          超时时间（秒）
     * @return 执行结果
     */
    public ExecResult installRequirements(String containerId, String requirementsContainerPath, int timeoutSeconds) {
        return exec(containerId, new String[]{
            "pip", "install", "--no-cache-dir", "--user", "-r", requirementsContainerPath
        }, timeoutSeconds);
    }

    /**
     * 查询平台托管的所有容器
     */
    public List<ManagedContainer> listManagedContainers() {
        DockerClient client = clientProvider.getClient();
        List<Container> containers = client.listContainersCmd()
            .withShowAll(true)
            .withLabelFilter(Map.of(LABEL_APP, LABEL_APP_VALUE))
            .exec();
        List<ManagedContainer> result = new ArrayList<>(containers.size());
        for (Container container : containers) {
            String modelId = container.getLabels() == null ? null : container.getLabels().get(LABEL_MODEL_ID);
            String name = container.getNames() != null && container.getNames().length > 0
                ? container.getNames()[0].replaceFirst("^/", "")
                : null;
            result.add(new ManagedContainer(container.getId(), name, modelId, container.getStatus()));
        }
        return result;
    }

    /**
     * 按容器名删除同名容器（用于创建前清理）
     */
    private void removeByName(DockerClient client, String name) {
        List<Container> containers = client.listContainersCmd()
            .withShowAll(true)
            .withNameFilter(List.of(name))
            .exec();
        String expected = "/" + name;
        for (Container container : containers) {
            String[] names = container.getNames();
            if (names == null) {
                continue;
            }
            for (String n : names) {
                if (expected.equals(n)) {
                    try {
                        client.removeContainerCmd(container.getId()).withForce(true).withRemoveVolumes(false).exec();
                        log.info("清理同名残留容器, name => {}, containerId => {}", name, container.getId());
                    } catch (NotFoundException | ConflictException e) {
                        log.debug("清理同名容器忽略异常: {}", e.getMessage());
                    }
                    break;
                }
            }
        }
    }

    private void closeQuietly(ResultCallback.Adapter<Frame> callback) {
        try {
            callback.close();
        } catch (IOException ignored) {
            // ignore
        }
    }

    /**
     * 容器命令执行结果
     *
     * @param stdout   标准输出
     * @param stderr   标准错误
     * @param exitCode 退出码（-1 表示未知或超时）
     * @param timeout  是否超时
     */
    public record ExecResult(String stdout, String stderr, int exitCode, boolean timeout) {

        /**
         * 是否执行成功
         */
        public boolean success() {
            return !timeout && exitCode == 0;
        }
    }

    /**
     * 平台托管容器信息
     *
     * @param id      容器ID
     * @param name    容器名
     * @param modelId 关联模型ID
     * @param status  容器状态
     */
    public record ManagedContainer(String id, String name, String modelId, String status) {
    }

}
