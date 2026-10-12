package org.dromara.model.runtime;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * DockerClient 单例提供者
 *
 * <p>参照 {@code OssFactory} 的单例风格，使用 volatile + 双重检查锁延迟构建，
 * 避免容器未就绪或 docker 服务未启动时影响应用启动。
 * 应用关闭时通过 {@link #destroy()} 释放底层连接。</p>
 *
 * @author model-platform
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DockerClientProvider implements DisposableBean {

    private final RuntimeProperties properties;

    /**
     * DockerClient 单例（延迟初始化）
     */
    private volatile DockerClient dockerClient;

    /**
     * 获取 DockerClient 单例，首次调用时构建并做连通性探测
     */
    public DockerClient getClient() {
        if (dockerClient == null) {
            synchronized (this) {
                if (dockerClient == null) {
                    dockerClient = build();
                }
            }
        }
        return dockerClient;
    }

    /**
     * 构建 DockerClient
     */
    private DockerClient build() {
        String host = properties.getDockerHost();
        log.info("初始化 DockerClient, dockerHost => {}", host);
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
            .withDockerHost(host)
            .build();
        DockerHttpClient httpClient = new ZerodepDockerHttpClient.Builder()
            .dockerHost(config.getDockerHost())
            .sslConfig(config.getSSLConfig())
            .maxConnections(100)
            .connectionTimeout(Duration.ofSeconds(properties.getConnectTimeoutSeconds()))
            .responseTimeout(Duration.ofSeconds(properties.getResponseTimeoutSeconds()))
            .build();
        DockerClient client = DockerClientImpl.getInstance(config, httpClient);
        // 连通性探测，失败时抛出异常由调用方感知
        client.pingCmd().exec();
        log.info("DockerClient 初始化成功, dockerHost => {}", host);
        return client;
    }

    @Override
    public void destroy() {
        DockerClient client = this.dockerClient;
        if (client != null) {
            try {
                client.close();
                log.info("DockerClient 已关闭");
            } catch (Exception e) {
                log.warn("关闭 DockerClient 异常", e);
            }
        }
    }

}
