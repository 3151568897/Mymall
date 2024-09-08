package com.example.mymall.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class MyFeignConfig {

//    @Bean
//    public SeataHandlerInterceptor seataHandlerInterceptor(){
//        return new SeataHandlerInterceptor();
//    }

    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor() {

            @Override
            public void apply(RequestTemplate requestTemplate) {
                //1.拿到一开始进入的请求的session
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                if (requestAttributes != null) {
                    HttpServletRequest request = request = requestAttributes.getRequest();
                    //2.同步请求头信息,主要是cookie
                    requestTemplate.header("Cookie",request.getHeader("Cookie"));
                }
//                // 解决seata的xid未传递
//                String xid = RootContext.getXID();
//                if (StringUtils.isNotEmpty(xid)) {
//                    requestTemplate.header(RootContext.KEY_XID, xid);
//                }
            }
        };
    }
}
