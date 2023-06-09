package com.zhouj.rpc.proxy;

import com.zhouj.rpc.client.ClientHandler;
import com.zhouj.rpc.client.ConnectManager;
import com.zhouj.rpc.client.ResponseFuture;
import com.zhouj.rpc.constant.Constant;
import com.zhouj.rpc.protocol.Request;
import com.zhouj.rpc.protocol.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 消费者接口动态代理
 *
 * @author zhouj
 * @since 2020-08-03
 */
public class ClientInvocationHandler implements InvocationHandler {

    Logger log = LoggerFactory.getLogger(ClientInvocationHandler.class);

    private Class<?> type;

    public ClientInvocationHandler(Class type) {
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
        int i = 2;
        do {
            if (atomicInteger.get() > 0) {
                log.info("重试次数:" + atomicInteger.get());
            }
            response = request(request, atomicInteger);
        } while (response != null && response.getCode() == Constant.TIME_OUT && atomicInteger.get() < i);

        if (response == null) {
            return null;
        } else {
            return response.getResult();
        }
    }

    public Response request(Request request, AtomicInteger atomicInteger) {
        try {
            ConnectManager connectManager = ConnectManager.getInstance();
            ClientHandler clientHandler = connectManager.roundHandle(type.getName());
            ResponseFuture rpcFuture = clientHandler.sendRequest(request);
            return rpcFuture.get(Constant.SESSION_TIMEOUT, TimeUnit.MILLISECONDS);
        } finally {
            atomicInteger.incrementAndGet();
        }
    }
}
