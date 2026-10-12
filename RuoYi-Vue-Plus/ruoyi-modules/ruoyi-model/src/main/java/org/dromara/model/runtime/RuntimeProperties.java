package org.dromara.model.runtime;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 模型运行时（Docker 沙箱容器）配置属性
 *
 * <p>对应 application.yml 中 {@code model.runtime.*} 配置。</p>
 *
 * @author model-platform
 */
@Data
@Component
@ConfigurationProperties(prefix = "model.runtime")
public class RuntimeProperties {

    /**
     * 运行时总开关，关闭后发布/运行将降级为不依赖 Docker 的模式
     */
    private Boolean enabled = Boolean.TRUE;

    /**
     * Docker 服务地址（Windows Docker Desktop 默认为 npipe）
     */
    private String dockerHost = "npipe:////./pipe/dockerDesktopLinuxEngine";

    /**
     * 运行时基础镜像
     */
    private String image = "model-python-runtime:1.0";

    /**
     * 宿主机脚本目录（bind mount 源目录），已发布模型的脚本会物化到此处
     */
    private String scriptDir = "D:\\AAA_WorkProject\\ai-model\\runtime-scripts";

    /**
     * 容器内脚本目录（bind mount 目标目录）
     */
    private String containerScriptDir = "/app/scripts";

    /**
     * 容器内 runner.py 路径（镜像内固定位置）
     */
    private String runnerPath = "/app/runner.py";

    /**
     * 单个容器内存限制（MB）
     */
    private Long memoryMb = 1024L;

    /**
     * 单个容器 CPU 核数限制
     */
    private Double cpus = 1.0;

    /**
     * 单个容器进程数限制
     */
    private Long pidsLimit = 128L;

    /**
     * 单次脚本执行超时时间（秒）
     */
    private Integer execTimeoutSeconds = 60;

    /**
     * 安装 requirements.txt 依赖的超时时间（秒）
     */
    private Integer installTimeoutSeconds = 600;

    /**
     * 容器网络模式
     */
    private String network = "host";

    /**
     * 最大并发执行数
     */
    private Integer maxConcurrentExec = 4;

    /**
     * Docker 连接超时时间（秒）
     */
    private Integer connectTimeoutSeconds = 30;

    /**
     * Docker 请求响应超时时间（秒）
     */
    private Integer responseTimeoutSeconds = 300;

    /**
     * 容器内脚本访问平台数据库的主机地址
     *
     * <p>Docker Desktop 下容器需通过 host.docker.internal 访问宿主机 MySQL。</p>
     */
    private String dbHost = "host.docker.internal";

    /**
     * 容器内脚本访问平台数据库的端口
     */
    private Integer dbPort = 3306;

    /**
     * 容器内脚本访问的平台数据库名
     */
    private String dbName = "ai-model";

    /**
     * 容器内脚本访问平台数据库的只读账号
     */
    private String dbUser = "model_reader";

    /**
     * 容器内脚本访问平台数据库的只读账号密码
     */
    private String dbPassword = "";

}
