package com.zhouj.rpc.client.registry;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhouj
 * @since 2023-03-13
 */
public abstract class AbstractConsumerRegistry implements ConsumerRegistry {

    Map<String, Consumer> consumers = new ConcurrentHashMap<>();

    public List<Consumer> getConsumers() {
        return this.consumers.values().stream().collect(Collectors.toList());
    }

    public void registry(Consumer consumer) {
        consumers.put(consumer.getServiceName(), consumer);
    }

    public void registry(Class<?> aClass) {
        Consumer consumer = new DefaultConsumer(aClass);
        consumers.put(consumer.getServiceName(), consumer);
    }

    public void registry(List<Consumer> consumers) {
        this.consumers.putAll(consumers.stream().collect(Collectors.toMap(Consumer::getServiceName, Function.identity())));
    }


}
