package com.zhouj.rpc.invoker;

import com.zhouj.rpc.protocol.Request;

/**
 * 方法调用接口
 *
 * @author zhouj
 * @since 2023-03-16
 */
public interface MethodInvoker {

    Object invoke(Request request,Class<?> aClass);
}
