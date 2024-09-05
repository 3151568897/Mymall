package com.example.mymall;


import com.example.mymall.product.MyMallProductApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest(classes = MyMallProductApplication.class)
class MyMallProductApplicationTests {

    public ExecutorService service = Executors.newFixedThreadPool(5);

    @Test
    void contextLoads() {

    }

    @Test
    void completableFutureTest(){
        System.out.println("服务开始");
        CompletableFuture.supplyAsync(()->{
            System.out.println("111");
            return "111";
        }, service).whenCompleteAsync((res, exception) -> {
            System.out.println("t = " + res);
            System.out.println("u = " + exception);
        });

        System.out.println("服务结束");
    }

}
