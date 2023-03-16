package com.zhouj.rpc.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zhouj
 * @since 2020-08-04
 */
@SpringBootApplication
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ConsumerApplication.class);
        springApplication.run(args);
    }
}
