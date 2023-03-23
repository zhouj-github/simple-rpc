package com.zhouj.rpc.client.registry;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhouj
 * @since 2023-03-13
 */
public abstract class AbstractConsumerRegistry implements ConsumerRegistry {

    Map<String, Consumer> consumerCache = new ConcurrentHashMap<>();

    @Override
    public List<Consumer> getConsumerCache() {
        return this.consumerCache.values().stream().collect(Collectors.toList());
    }

    @Override
    public void registry(Consumer consumer) {
        consumerCache.put(consumer.getServiceName(), consumer);
    }

    @Override
    public void registry(Class<?> aClass) {
        Consumer consumer = new DefaultConsumer(aClass);
        consumerCache.put(consumer.getServiceName(), consumer);
    }

    @Override
    public void registry(List<Consumer> consumers) {
        this.consumerCache.putAll(consumers.stream().collect(Collectors.toMap(Consumer::getServiceName, Function.identity())));
    }


}
