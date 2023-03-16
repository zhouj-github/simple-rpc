package com.zhouj.rpc.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zhouj
 * @since 2020-08-04
 */
@SpringBootApplication
public class ServerApplication {


    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ServerApplication.class);
        springApplication.run(args);
    }

}
