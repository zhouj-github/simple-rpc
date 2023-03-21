package com.zhouj.rpc.invoker;

import com.zhouj.rpc.protocol.Request;
import com.zhouj.rpc.util.ProtostuffUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 默认服务端方法调用实现
 *
 * @author zhouj
 * @since 2023-03-16
 */
public class DefaultMethodInvoker implements MethodInvoker {

    private volatile Map<String, Object> serviceMap = new HashMap<>();

    private Logger logger = LoggerFactory.getLogger(DefaultMethodInvoker.class);


    @Override
    public Object invoke(Request request, Class<?> aClass) {
        Object o = serviceMap.get(aClass.getCanonicalName());
        if (o == null) {
            synchronized (this) {
                //双重检查锁
                o = serviceMap.get(aClass.getCanonicalName());
                if (o == null) {
                    Map<String, Object> map = new HashMap<>(serviceMap.size() + 1);
                    map.putAll(serviceMap);
                    o = ProtostuffUtil.objenesis.newInstance(aClass);
                    map.put(aClass.getCanonicalName(), o);
                    serviceMap = map;
                }
            }
        }
        try {
            Method method = aClass.getMethod(request.getMethodName(), request.getParamTypes());
            return method.invoke(o, request.getParams());
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
