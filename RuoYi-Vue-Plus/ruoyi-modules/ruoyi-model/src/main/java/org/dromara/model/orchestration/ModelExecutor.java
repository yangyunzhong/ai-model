package org.dromara.model.orchestration;

/**
 * 模型执行器
 *
 * <p>负责把一次 {@link ModelExecContext} 真正跑起来并给出 {@link ExecuteResult}。
 * 实现类通过 Spring 的 {@code @Order} 决定优先级，由 {@link ModelExecOrchestrator}
 * 按顺序选取第一个 {@link #supports} 为 true 的执行器。</p>
 *
 * <p>当前实现：</p>
 * <ul>
 *     <li>{@link DockerModelExecutor}：容器可用时在 Docker 沙箱内执行真实脚本。</li>
 *     <li>{@link MockModelExecutor}：兜底，生成确定性的模拟健康度结果。</li>
 * </ul>
 *
 * @author model-platform
 */
public interface ModelExecutor {

    /**
     * 是否支持处理该上下文
     */
    boolean supports(ModelExecContext context);

    /**
     * 执行
     */
    ExecuteResult execute(ModelExecContext context);

}
