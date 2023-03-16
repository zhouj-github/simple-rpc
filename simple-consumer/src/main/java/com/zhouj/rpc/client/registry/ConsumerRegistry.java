package com.zhouj.rpc.client.registry;

import java.util.List;

/**
 * 消费者注册中心
 *
 * @author zhouj
 * @since 2023-03-13
 */
public interface ConsumerRegistry {

    List<Consumer> getConsumers();

    void registry(Class<?> aClass);

    void registry(Consumer consumer);

    void registry(List<Consumer> consumers);


}
