package com.my.newcoder.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class BeanConfig
{
    @Bean(name = "cachedThreadPool")
    public ExecutorService getCachedThreadPoll()
    {
        ExecutorService service= Executors.newCachedThreadPool();
        return service;
    }

}
