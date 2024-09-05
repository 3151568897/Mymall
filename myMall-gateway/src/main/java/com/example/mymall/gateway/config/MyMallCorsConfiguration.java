package com.example.mymall.gateway.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import reactor.core.publisher.Mono;

@Configuration
public class MyMallCorsConfiguration {

    @Bean
    public CorsWebFilter corsWebFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration config = new CorsConfiguration();
        // 允许所有头部
        config.addAllowedHeader("*");
        // 允许所有方法
        config.addAllowedMethod("*");
        // 允许的域
        config.addAllowedOrigin("http://localhost:8001");
        // 允许凭证
        config.setAllowCredentials(true);
        // 设置跨域请求的缓存时间
        config.setMaxAge(3600L);

        // 注册 CORS 配置
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }


    @Bean
    public GlobalFilter errorHandlingFilter() {
        return (exchange, chain) -> chain.filter(exchange)
                .onErrorResume(ex -> {
                    if (exchange.getResponse().isCommitted()) {
                        // 日志记录，防止已提交的响应再次触发错误
                        return Mono.error(ex);
                    }
                    exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                    return exchange.getResponse().setComplete();
                });
    }

//    //基于 Reactive 的重试机制
//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route("register_route", r -> r.path("/register.html")
//                        .filters(f -> f.retry(config -> config.setRetries(3)
//                                .setMethods(HttpMethod.GET)))
//                        .uri("http://auth.mymall.com"))
//                .build();
//    }


}
