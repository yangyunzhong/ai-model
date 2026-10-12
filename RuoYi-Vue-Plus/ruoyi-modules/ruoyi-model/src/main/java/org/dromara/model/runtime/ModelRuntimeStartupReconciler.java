package org.dromara.model.runtime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.model.service.IModelRuntimeService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 模型运行时启动对账器
 *
 * <p>应用启动后触发一次容器对账：使「已发布模型」与其沙箱容器状态保持一致，
 * 并清理平台遗留的孤儿容器。对账失败不影响应用启动。</p>
 *
 * @author model-platform
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ModelRuntimeStartupReconciler implements ApplicationRunner {

    private final IModelRuntimeService modelRuntimeService;

    @Override
    public void run(ApplicationArguments args) {
        modelRuntimeService.reconcileOnStartup();
    }

}
