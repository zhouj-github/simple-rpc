package com.zhouj.rpc.invoker;

import com.zhouj.rpc.protocol.Request;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 默认服务端方法调用实现
 * @author zhouj
 * @since 2023-03-16
 */
public class DefaultMethodInvoker implements MethodInvoker {


    @Override
    public Object invoke(Request request, Class<?> aClass) {
        try {
            Object o = aClass.newInstance();
            Method method = aClass.getMethod(request.getMethodName(), request.getParamTypes());
            return method.invoke(o, request.getParams());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }
}
