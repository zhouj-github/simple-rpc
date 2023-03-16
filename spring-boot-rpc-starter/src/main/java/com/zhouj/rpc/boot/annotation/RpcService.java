package com.zhouj.rpc.boot.annotation;

import java.lang.annotation.*;

/**
 * @author zhouj
 * @since 2023-03-09
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcService {
}
