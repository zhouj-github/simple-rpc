package com.zhouj.rpc.proxy;

import com.zhouj.rpc.client.ClientHandler;
import com.zhouj.rpc.client.ConnectManager;
import com.zhouj.rpc.client.RpcFuture;
import com.zhouj.rpc.protocol.Request;
import com.zhouj.rpc.protocol.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 消费者接口动态代理
 *
 * @author zhouj
 * @since 2020-08-03
 */
public class ProxyObject implements InvocationHandler {

    Logger log = LoggerFactory.getLogger(ProxyObject.class);

    private Class<?> type;

    public ProxyObject(Class type) {
        this.type = type;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        Request request = new Request();
        request.setInterfaceName(type.getName());
        request.setMethodName(method.getName());
        request.setParamTypes(method.getParameterTypes());
        request.setParams(args);
        request.setRequestId(UUID.randomUUID().toString());
        request.setTimestamp(System.currentTimeMillis());
        AtomicInteger atomicInteger = new AtomicInteger(0);
        Response response;
        //超时重试,重试时重新获取连接
        do {
            response = request(request);
        } while (response != null && response.getCode() == 300 && atomicInteger.get() < 2);
        if (response == null) {
            return null;
        } else {
            return response.getResult();
        }
    }

    public Response request(Request request) {
        try {
            ConnectManager connectManager = ConnectManager.getInstance();
            ClientHandler clientHandler = connectManager.getRoundRobinHandle(type.getName());
            RpcFuture rpcFuture = clientHandler.sendRequest(request);
            return rpcFuture.get(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;


    }
}
