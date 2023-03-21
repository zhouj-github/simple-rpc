package com.zhouj.rpc.invoker;

import com.zhouj.rpc.protocol.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 默认服务端方法调用实现
 *
 * @author zhouj
 * @since 2023-03-16
 */
public class DefaultMethodInvoker implements MethodInvoker {

    private Logger logger = LoggerFactory.getLogger(DefaultMethodInvoker.class);


    @Override
    public Object invoke(Request request, Class<?> aClass) {
        try {
            Object o = aClass.newInstance();
            Method method = aClass.getMethod(request.getMethodName(), request.getParamTypes());
            return method.invoke(o, request.getParams());
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }
}
