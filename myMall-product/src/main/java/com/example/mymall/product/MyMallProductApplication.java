package com.example.mymall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;


@MapperScan("com.example.mymall.product.dao")
@EnableFeignClients(basePackages = "com.example.mymall.product.feign")
@SpringBootApplication
@EnableDiscoveryClient
public class MyMallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyMallProductApplication.class, args);
    }

}
