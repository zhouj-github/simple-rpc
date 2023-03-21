package com.zhouj.rpc.proxy;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * @author zhouj
 * @since 2020-08-03
 */
public class ProxyFactory implements FactoryBean<Object> {

    public Class<?> type;

    @Override
    public Object getObject() {
        return Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, new ClientProxy(type));
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }
}
