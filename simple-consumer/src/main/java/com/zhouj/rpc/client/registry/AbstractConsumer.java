package com.zhouj.rpc.client.registry;

/**
 * @author zhouj
 * @since 2023-03-13
 */
public abstract class AbstractConsumer implements Consumer {

    private String serviceName;

    private Class<?> aClass;

    public AbstractConsumer(Class<?> aClass) {
        this.aClass = aClass;
        this.serviceName = aClass.getCanonicalName();
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    @Override
    public Consumer create(Class<?> aClass) {
        this.serviceName = aClass.getCanonicalName();
        this.aClass = aClass;
        return this;
    }

    @Override
    public Class<?> getConsumerClass() {
        return this.aClass;
    }

    public int compareTo(Consumer consumer) {
        return this.getServiceName().equals(consumer.getServiceName()) ? 1 : 0;
    }


}
