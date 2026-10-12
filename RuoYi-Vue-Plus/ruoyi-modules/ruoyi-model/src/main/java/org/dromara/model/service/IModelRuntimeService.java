package org.dromara.model.service;

/**
 * 模型运行时Service接口
 *
 * <p>负责「已发布模型」的容器生命周期：
 * 发布（物化脚本 + 建容器 + 安装依赖）、下架（停容器 + 删容器 + 清脚本目录），
 * 以及应用启动时的容器对账。</p>
 *
 * @author model-platform
 */
public interface IModelRuntimeService {

    /**
     * 发布模型：物化脚本到宿主机并创建/启动对应沙箱容器
     *
     * @param modelId 模型ID
     */
    void publish(String modelId);

    /**
     * 下架模型：停止并删除沙箱容器，清理宿主脚本目录
     *
     * @param modelId 模型ID
     */
    void offline(String modelId);

    /**
     * 彻底销毁模型运行时物料：停止并删除沙箱容器、清理宿主脚本目录、
     * 删除 MinIO 中的脚本对象及对应的 model_script 记录
     *
     * <p>用于删除模型定义前的物料清理，保证数据库记录与对象存储不留残留。</p>
     *
     * @param modelId 模型ID
     */
    void destroy(String modelId);

    /**
     * 应用启动时对账：同步已发布模型与容器状态，清理孤儿容器
     */
    void reconcileOnStartup();

}
