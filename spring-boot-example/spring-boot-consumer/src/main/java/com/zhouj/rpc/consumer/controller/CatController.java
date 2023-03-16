package com.zhouj.rpc.consumer.controller;

import com.zhouj.rpc.boot.annotation.RpcClient;
import com.zhouj.rpc.server.api.CatService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhouj
 * @since 2023-03-16
 */
@RestController
public class CatController {

    @RpcClient
    private CatService catService;

    @RequestMapping("/cat")
    public String test() {
        return catService.cat();
    }
}
