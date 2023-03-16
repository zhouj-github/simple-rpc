package com.zhouj.rpc.consumer.controller;

import com.zhouj.rpc.boot.annotation.RpcClient;
import com.zhouj.rpc.server.api.DogService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhouj
 * @since 2023-03-16
 */
@RestController
public class DogController {

    @RpcClient
    private DogService dogService;

    @RequestMapping("/dog")
    public String test() {
        return dogService.dog();
    }
}
