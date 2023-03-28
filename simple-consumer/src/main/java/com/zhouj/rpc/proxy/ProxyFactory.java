package com.zhouj.rpc.proxy;

import com.zhouj.rpc.client.registry.Consumer;
import com.zhouj.rpc.config.RpcConfig;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.lang.reflect.InvocationHandler;

/**
 * 代理工厂类
 *
 * @author zhouj
 * @since 2023-03-23
 */
public class ProxyFactory {

    public static Object getProxy(ClassLoader classLoader, Class<?> targetClass, InvocationHandler handler) {

        try {
            return JavassistProxy.newProxyInstance(classLoader, targetClass, handler);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        try {
            JavassistProxy.newProxyInstance(ClassLoader.getSystemClassLoader(), RpcConfig.class, new ClientInvocationHandler(Consumer.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
