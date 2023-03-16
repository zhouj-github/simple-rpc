package com.zhouj.rpc.consumer.controller;

import com.zhouj.rpc.boot.annotation.RpcClient;
import com.zhouj.rpc.server.api.CatService;
import com.zhouj.rpc.server.api.DogService;
import com.zhouj.rpc.server.api.RemoteService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhouj
 * @since 2020-08-04
 */
@RestController
public class ConsumerController {


    @RpcClient
    private RemoteService remoteService;

    @RpcClient
    private CatService catService;

    @RpcClient
    private DogService dogService;

    @RequestMapping("/remote")
    public String test() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(remoteService.remote());
        stringBuilder.append(catService.cat());
        stringBuilder.append(dogService.dog());
        return stringBuilder.toString();
    }
}
