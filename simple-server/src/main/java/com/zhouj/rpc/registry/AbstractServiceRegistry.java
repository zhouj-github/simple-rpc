package com.zhouj.rpc.registry;

import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhouj
 * @since 2023-03-13
 */
public abstract class AbstractServiceRegistry implements ServiceRegistry {

    private ConcurrentHashMap<String, ServiceInfo> map = new ConcurrentHashMap<>();

    @Override
    public void registryService(Map<String,ServiceInfo> serviceInfoMap) {
        if (CollectionUtils.isEmpty(serviceInfoMap)) {
            return;
        }
        map.putAll(serviceInfoMap);
    }

    @Override
    public ServiceInfo getService(String serviceName) {
        return map.get(serviceName);
    }

    public Map<String, ServiceInfo> getServices() {
        return this.map;
    }

}
