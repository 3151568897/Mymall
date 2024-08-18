package com.example.mymall.thirdparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MyMallThirdPartyApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyMallThirdPartyApplication.class, args);
    }

}
