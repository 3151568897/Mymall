package com.example.mymall.ware.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class MyThreadConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties properties) {
        return new ThreadPoolExecutor(properties.getCorePoolSize(),
                properties.getMaxPoolSize(),
                properties.getKeepAliveTime(),
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(10000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }
}
