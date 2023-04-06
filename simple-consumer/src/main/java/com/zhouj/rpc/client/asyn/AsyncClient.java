package com.zhouj.rpc.client.asyn;

import com.zhouj.rpc.call.Callable;
import com.zhouj.rpc.client.ClientHandler;
import com.zhouj.rpc.client.ConnectManager;
import com.zhouj.rpc.client.ResponseFuture;
import com.zhouj.rpc.protocol.Request;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author zhouj
 * @since 2023-04-06
 */
public class AsyncClient {

    /**
     *
     * @param interfaceClass
     * @param methodName
     * @param args
     * @return
     */
    public static ResponseFuture async(Class<?> interfaceClass, String methodName, Object[] args) {
        Request request = buildRequest(interfaceClass, methodName, args);
        ResponseFuture rpcFuture = ConnectManager.getInstance().roundHandle(interfaceClass.getName()).sendRequest(request);
        return rpcFuture;
    }


    /**
     * 回调接口
     * @param interfaceClass
     * @param methodName
     * @param args
     * @param callable
     */
    public static void call(Class<?> interfaceClass, String methodName, Object[] args, Callable callable) {
        Request request = buildRequest(interfaceClass, methodName, args);
        ClientHandler clientHandler = ConnectManager.getInstance().roundHandle(interfaceClass.getName());
        clientHandler.sendCall(request, callable);
    }

    private static Request buildRequest(Class<?> interfaceClass, String methodName, Object[] args) {
        Request request = new Request();
        request.setInterfaceName(interfaceClass.getName());
        request.setMethodName(methodName);
        if (args != null) {
            Class<?>[] classes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                classes[i] = args[i].getClass();
            }
            Method method = null;
            try {
                method = interfaceClass.getMethod(methodName, classes);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            request.setParamTypes(method.getParameterTypes());
        }
        request.setParams(args);
        request.setRequestId(UUID.randomUUID().toString());
        request.setTimestamp(System.currentTimeMillis());
        return request;
    }
}
