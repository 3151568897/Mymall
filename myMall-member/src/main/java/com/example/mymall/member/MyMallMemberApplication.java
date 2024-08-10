package com.example.mymall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MyMallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyMallMemberApplication.class, args);
    }

}
