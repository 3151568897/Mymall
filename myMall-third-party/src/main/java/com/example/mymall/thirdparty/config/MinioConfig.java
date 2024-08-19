package com.example.mymall.thirdparty.config;

import io.minio.MinioClient;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class MinioConfig {

    @Value("${minio.ip}")
    private String ip;

    @Value("${minio.port}")
    private int port;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.MINUTES)
                .writeTimeout(30, TimeUnit.MINUTES)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        return MinioClient.builder()
                .httpClient(httpClient)
                .endpoint("http://"+ip+":"+port) //https or not
                .credentials(accessKey, secretKey)
                .build();
    }
}
