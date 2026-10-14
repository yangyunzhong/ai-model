package org.dromara.web.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MinIO 自动拉起组件：后端应用启动时检测本机 MinIO 服务是否就绪，
 * 未就绪则以最小化窗口自动启动 MinIO，并等待端口可用后继续应用启动流程。
 * <p>用途：开发环境免手动启动 MinIO（对象存储 sys_oss_config 默认指向 127.0.0.1:9000）。
 * 可通过 application.yml 的 minio.auto-start 配置开关与路径参数。</p>
 *
 * @author ai-model
 */
@Slf4j
@Component
public class MinioAutoLauncher implements ApplicationRunner {

    private final boolean enabled;
    private final String host;
    private final int port;
    private final String exePath;
    private final String dataDir;
    private final String rootUser;
    private final String rootPassword;
    private final int waitTimeoutSeconds;

    public MinioAutoLauncher(
        @Value("${minio.auto-start.enabled:true}") boolean enabled,
        @Value("${minio.auto-start.host:127.0.0.1}") String host,
        @Value("${minio.auto-start.port:9000}") int port,
        @Value("${minio.auto-start.exe-path:D:/minio/bin/minio.exe}") String exePath,
        @Value("${minio.auto-start.data-dir:D:/minio/data}") String dataDir,
        @Value("${minio.auto-start.root-user:}") String rootUser,
        @Value("${minio.auto-start.root-password:}") String rootPassword,
        @Value("${minio.auto-start.wait-timeout-seconds:15}") int waitTimeoutSeconds) {
        this.enabled = enabled;
        this.host = host;
        this.port = port;
        this.exePath = exePath;
        this.dataDir = dataDir;
        this.rootUser = rootUser;
        this.rootPassword = rootPassword;
        this.waitTimeoutSeconds = waitTimeoutSeconds;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!enabled) {
            return;
        }
        if (isPortOpen()) {
            log.info("[MinIO自动拉起] MinIO 已在 {}:{} 运行，跳过启动", host, port);
            return;
        }
        if (!Files.isExecutable(Path.of(exePath))) {
            log.warn("[MinIO自动拉起] 未找到 MinIO 可执行文件: {}，跳过自动启动", exePath);
            return;
        }
        try {
            startMinio();
        } catch (IOException e) {
            log.error("[MinIO自动拉起] 启动 MinIO 进程失败", e);
            return;
        }
        waitReady();
    }

    /**
     * 以最小化窗口启动 MinIO（Windows），保留窗口便于查看日志与手动停止。
     */
    private void startMinio() throws IOException {
        List<String> command = new ArrayList<>();
        command.add("cmd");
        command.add("/c");
        command.add("start");
        command.add("/min");
        command.add(exePath);
        command.add("server");
        command.add(dataDir);
        command.add("--console-address");
        command.add(":9001");

        ProcessBuilder builder = new ProcessBuilder(command);
        Map<String, String> env = builder.environment();
        if (rootUser != null && !rootUser.isBlank() && rootPassword != null && !rootPassword.isBlank()) {
            env.put("MINIO_ROOT_USER", rootUser);
            env.put("MINIO_ROOT_PASSWORD", rootPassword);
            log.info("[MinIO自动拉起] 使用配置的 root 凭证启动 MinIO");
        }
        builder.start();
        log.info("[MinIO自动拉起] 已发起 MinIO 启动进程: {} server {}（最小化窗口）", exePath, dataDir);
    }

    /**
     * 轮询等待 MinIO 端口就绪，超时仅告警不阻断应用启动。
     */
    private void waitReady() {
        long deadline = System.currentTimeMillis() + waitTimeoutSeconds * 1000L;
        while (System.currentTimeMillis() < deadline) {
            if (isPortOpen()) {
                log.info("[MinIO自动拉起] MinIO 已就绪: http://{}:{}（控制台 :9001）", host, port);
                return;
            }
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        log.warn("[MinIO自动拉起] 等待 {}s 后 MinIO 端口 {}:{} 仍未就绪，请检查 MinIO 手动启动", waitTimeoutSeconds, host, port);
    }

    private boolean isPortOpen() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 500);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
