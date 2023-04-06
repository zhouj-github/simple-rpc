package com.zhouj.rpc.call;

import com.zhouj.rpc.protocol.Response;

/**
 * @author zhouj
 * @since 2023-04-06
 */
@FunctionalInterface
public interface Callable {

    void call(Response response);
}
