package com.zhouj.rpc.server.service;

import com.zhouj.rpc.boot.annotation.RpcService;
import com.zhouj.rpc.server.api.DogService;

/**
 * @author zhouj
 * @since 2023-03-15
 */
@RpcService
public class DogServiceImpl implements DogService, TestService {
    @Override
    public String dog() {
        return "I am dog";
    }
}
