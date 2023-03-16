package com.zhouj.rpc.registry;

/**
 * 服务信息接口
 *
 * @author zhouj
 * @since 2023-03-10
 */
public interface ServiceInfo {

    ServiceInfo createService(Class<?> aClass);

    String getInterfaceName();

    Class<?> getServiceClass();
}
