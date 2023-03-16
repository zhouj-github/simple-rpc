package com.zhouj.rpc.server.service;

import com.zhouj.rpc.boot.annotation.RpcService;
import com.zhouj.rpc.server.api.CatService;
import com.zhouj.rpc.server.api.RemoteService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zhouj
 * @since 2023-03-15
 */
@RpcService
public class CatServiceImpl implements CatService {

    @Autowired
    private RemoteService remoteService;

    @Override
    public String cat() {
        return "I am cat";
    }
}
