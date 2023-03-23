package com.zhouj.rpc.proxy;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * @author zhouj
 * @since 2023-03-23
 */
public class ProxyFactory {

    public Object getObject() {

        return null;
    }

    public static void main(String[] args) {
        try {
            JavassistProxy.newProxyInstance(ClassLoader.getSystemClassLoader(), DefaultListableBeanFactory.class, new ClientInvocationHandler(BeanDefinitionRegistry.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
