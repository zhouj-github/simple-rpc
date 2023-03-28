package com.zhouj.rpc.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author zhouj
 * @since 2020-08-03
 */
public class ClientFactory implements FactoryBean<Object> {

    private Logger logger = LoggerFactory.getLogger(ClientFactory.class);

    public Class<?> type;

    @Override
    public Object getObject() {
        try {
            return ProxyFactory.getProxy(type.getClassLoader(), type, new ClientInvocationHandler(type));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
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
