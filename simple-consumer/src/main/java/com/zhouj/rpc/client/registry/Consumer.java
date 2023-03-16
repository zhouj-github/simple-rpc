package com.zhouj.rpc.client.registry;

/**
 * 服务消费者
 *
 * @author zhouj
 * @since 2023-03-13
 */
public interface Consumer extends Comparable<Consumer>{

    String getServiceName();

    Consumer create(Class<?> aClass);

    Class<?> getConsumerClass();

}
