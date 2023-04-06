package com.zhouj.rpc.consumer.controller;

import com.zhouj.rpc.boot.annotation.RpcClient;
import com.zhouj.rpc.client.ResponseFuture;
import com.zhouj.rpc.protocol.Response;
import com.zhouj.rpc.server.api.CatService;
import com.zhouj.rpc.server.api.DogService;
import com.zhouj.rpc.server.api.RemoteService;
import com.zhouj.rpc.client.asyn.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhouj
 * @since 2020-08-04
 */
@RestController
public class ConsumerController {

    private Logger logger = LoggerFactory.getLogger(ConsumerController.class);


    @RpcClient
    private RemoteService remoteService;

    @RpcClient
    private CatService catService;

    @RpcClient
    private DogService dogService;

    @RequestMapping("/remote")
    public String remote() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(remoteService.remote() + "\n");
        stringBuilder.append(catService.cat() + "\n");
        stringBuilder.append(dogService.dog() + "\n");
        return stringBuilder.toString();
    }

    @RequestMapping("/async")
    public String async() {
        ResponseFuture responseFuture = AsyncClient.async(RemoteService.class, "remote", null);
        Response response = responseFuture.get();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(response.getResult());
        return stringBuilder.toString();
    }

    @RequestMapping("/call")
    public void call() {
        AsyncClient.call(RemoteService.class, "remote", null, response -> {
            logger.info("回调结果:{}", response.getResult());
        });

    }


}
