package com.zhouj.rpc.proxy;

import com.zhouj.rpc.client.ClientHandler;
import com.zhouj.rpc.client.ConnectManager;
import com.zhouj.rpc.client.RpcFuture;
import com.zhouj.rpc.protocol.Request;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * 消费者接口动态代理
 * @author zhouj
 * @since 2020-08-03
 */
public class ProxyObject implements InvocationHandler {

    private Class<?> type;

    public ProxyObject(Class type) {
        this.type = type;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ConnectManager connectManager = ConnectManager.getInstance();
        ClientHandler clientHandler = connectManager.getRoundRobinHandle(type.getName());
        Request request = new Request();
        request.setInterfaceName(type.getName());
        request.setMethodName(method.getName());
        request.setParamTypes(method.getParameterTypes());
        request.setParams(args);
        request.setRequestId(UUID.randomUUID().toString());
        request.setTimestamp(System.currentTimeMillis());
        RpcFuture rpcFuture = clientHandler.sendRequest(request);
        return rpcFuture.get();
    }
}
