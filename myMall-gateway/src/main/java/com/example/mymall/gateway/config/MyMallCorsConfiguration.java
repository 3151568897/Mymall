package com.example.mymall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;

@Configuration
public class MyMallCorsConfiguration {

    @Bean
    public CorsWebFilter corsWebFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration config = new CorsConfiguration();
        //配置跨域
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.addAllowedOriginPattern("*");
        config.setAllowCredentials(true);

        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
