package com.example.mymall.ware.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "mymall.thread")
@Configuration
@Data
public class ThreadPoolConfigProperties {

    private Integer corePoolSize;
    private Integer maxPoolSize;
    private Integer keepAliveTime;
}
