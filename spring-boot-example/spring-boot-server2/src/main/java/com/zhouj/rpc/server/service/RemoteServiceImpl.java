package com.zhouj.rpc.server.service;

import com.zhouj.rpc.boot.annotation.RpcService;
import com.zhouj.rpc.server.api.CatService;
import com.zhouj.rpc.server.api.RemoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhouj
 * @since 2023-03-09
 */
@RpcService
public class RemoteServiceImpl implements RemoteService {


    public String remote() {
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "远程服务2";
    }
}
