package com.zhouj.rpc.server.service;

import com.zhouj.rpc.boot.annotation.RpcService;
import com.zhouj.rpc.server.api.RemoteService;

/**
 * @author zhouj
 * @since 2023-03-09
 */
@RpcService
public class RemoteServiceImpl implements RemoteService {

    @Override
    public String remote() {
        return "远程服务2";
    }
}
