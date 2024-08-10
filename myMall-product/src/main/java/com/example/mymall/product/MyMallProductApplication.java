package com.example.mymall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@MapperScan("com.example.mymall.product.dao")
@SpringBootApplication
@EnableDiscoveryClient
public class MyMallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyMallProductApplication.class, args);
    }

}
