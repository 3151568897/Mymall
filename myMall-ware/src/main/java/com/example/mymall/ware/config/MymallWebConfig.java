package com.example.mymall.ware.config;

import com.example.mymall.ware.intercepotr.CartInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MymallWebConfig extends WebMvcConfigurationSupport {

    @Autowired
    CartInterceptor cartInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(cartInterceptor).addPathPatterns("/**");
//        registry.addInterceptor(new SeataHandlerInterceptor()).addPathPatterns("/**");
    }
}
