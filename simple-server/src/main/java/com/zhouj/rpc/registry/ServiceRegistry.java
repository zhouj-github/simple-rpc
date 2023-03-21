package com.zhouj.rpc.registry;


import java.util.Map;

/**
 * 暴露服务接口注册中心
 *
 * @author zhouj
 * @since 2023-03-10
 */
public interface ServiceRegistry {

    /**
     * 服务接口注册
     * @param serviceInfoMap
     */
    void registryService(Map<String,ServiceInfo> serviceInfoMap);

    /**
     * 获取服务接口信息
     * @param serviceName
     * @return
     */
    ServiceInfo getService(String serviceName);

    /***
     * 获取所有服务接口数据
     * @return
     */
    Map<String,ServiceInfo> getServices();

}
