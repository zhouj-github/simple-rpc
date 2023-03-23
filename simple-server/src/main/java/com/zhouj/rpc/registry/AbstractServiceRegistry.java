package com.zhouj.rpc.registry;

import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhouj
 * @since 2023-03-13
 */
public abstract class AbstractServiceRegistry implements ServiceRegistry {

    private ConcurrentHashMap<String, ServiceInfo> serviceCache = new ConcurrentHashMap<>();

    @Override
    public void registryService(Map<String,ServiceInfo> serviceInfoMap) {
        if (CollectionUtils.isEmpty(serviceInfoMap)) {
            return;
        }
        serviceCache.putAll(serviceInfoMap);
    }

    @Override
    public ServiceInfo getService(String serviceName) {
        return serviceCache.get(serviceName);
    }

    @Override
    public Map<String, ServiceInfo> getServices() {
        return this.serviceCache;
    }

}
