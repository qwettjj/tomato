
package com.example.tomatomall.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyWebMvcConfig implements WebMvcConfigurer {
    @Autowired
    LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/accounts/create")
                .excludePathPatterns("/api/accounts/login")
                .excludePathPatterns("/api/images")
                .excludePathPatterns("/api/ali/*")
                .excludePathPatterns("/api/alipay/pay")
                .excludePathPatterns("/api/accounts/getUserInfo")
                .order(1);
    }
}

