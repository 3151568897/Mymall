package com.example.mymall.thirdparty.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
        return MinioClient.builder()
                .endpoint(ip, port, false) //https or not
                .credentials(accessKey, secretKey)
                .build();
    }
}
