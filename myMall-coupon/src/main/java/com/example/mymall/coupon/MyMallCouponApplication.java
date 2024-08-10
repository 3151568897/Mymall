package com.example.mymall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MyMallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyMallCouponApplication.class, args);
    }

}
