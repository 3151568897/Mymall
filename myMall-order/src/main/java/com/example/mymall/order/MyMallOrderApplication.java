package com.example.mymall.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MyMallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyMallOrderApplication.class, args);
    }

}
