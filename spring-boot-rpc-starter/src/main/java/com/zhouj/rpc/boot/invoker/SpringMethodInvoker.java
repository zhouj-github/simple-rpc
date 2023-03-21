package com.zhouj.rpc.boot.invoker;

import com.zhouj.rpc.boot.context.ApplicationProvider;
import com.zhouj.rpc.invoker.MethodInvoker;
import com.zhouj.rpc.protocol.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author zhouj
 * @since 2023-03-16
 */
public class SpringMethodInvoker implements MethodInvoker {

    Logger logger = LoggerFactory.getLogger(SpringMethodInvoker.class);

    @Override
    public Object invoke(Request request, Class<?> aClass) {
        try {
            Object o = ApplicationProvider.applicationContext().getBean(aClass);
            Method method = aClass.getMethod(request.getMethodName(), request.getParamTypes());
            return method.invoke(o, request.getParams());
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }
}
