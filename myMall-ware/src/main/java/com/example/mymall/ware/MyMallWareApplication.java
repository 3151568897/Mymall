package com.example.mymall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.example.mymall.ware.dao")
@EnableTransactionManagement
@EnableFeignClients(basePackages = "com.example.mymall.ware.feign")
@EnableRedisHttpSession
public class MyMallWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyMallWareApplication.class, args);
    }

}
