package com.my.newcoder.configuration;

import com.my.newcoder.interceptor.LoginRequiredInterceptor;
import com.my.newcoder.interceptor.PassportInterceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
* 注册拦截器，并定义优先级
* */
@Component
public class MyWebConfiguration implements WebMvcConfigurer
{
    @Autowired
    PassportInterceptor passportInterceptor;

    @Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors (InterceptorRegistry registry)
    {
        registry.addInterceptor(passportInterceptor);
        registry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/user/*");
    }
}
