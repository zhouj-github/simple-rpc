package com.zhouj.rpc.server.service;

import com.zhouj.rpc.server.api.CatService;
import com.zhouj.rpc.server.api.RemoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhouj
 * @since 2023-03-16
 */
@Service
public class TestServiceImpl {

    @Autowired
    private RemoteService remoteService;

    @Autowired
    private CatService catService;
}
