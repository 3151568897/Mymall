package com.example.mymall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@MapperScan("com.example.mymall.product.dao")
@ComponentScan(basePackages = {"com.example.mymall.product"})
@SpringBootApplication
public class MyMallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyMallProductApplication.class, args);
    }

}
