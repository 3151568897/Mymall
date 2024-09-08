package com.example.mymall.order.config;

import com.example.mymall.order.intercepotr.LoginUserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class MymallWebConfig extends WebMvcConfigurationSupport {

    @Autowired
    LoginUserInterceptor loginUserInterceptor;
//    @Autowired
//    SeataHandlerInterceptor seataHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginUserInterceptor).addPathPatterns("/**").excludePathPatterns("/order/order/status/**");
//        registry.addInterceptor(seataHandlerInterceptor).addPathPatterns("/**");
    }
}
