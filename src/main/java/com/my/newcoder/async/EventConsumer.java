package com.my.newcoder.async;

import com.alibaba.fastjson.JSON;
import com.my.newcoder.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class EventConsumer implements InitializingBean, ApplicationContextAware
{
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private Map<EventType, List<EventHandler>> config = new HashMap<>();
    private ApplicationContext applicationContext;
    private ExecutorService workerGroup= Executors.newFixedThreadPool(4);

    @Autowired
    StringRedisTemplate redis;

    @Override
    public void afterPropertiesSet () throws Exception
    {
        initConfig();
        startConsumer();
    }

    private void initConfig ()
    {
        //获取所有的EventHandler，建立EventType与EventHandler的映射
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if(beans != null)
        {
            for (Map.Entry<String, EventHandler> entrys : beans.entrySet())
            {
                List<EventType> types = entrys.getValue().getSupportEventTypes();
                for (EventType type : types)
                {
                    if(!config.containsKey(type))
                    {
                        config.put(type, new ArrayList<EventHandler>());
                    }
                    config.get(type).add(entrys.getValue());
                }
            }
        }
    }

    private void startConsumer()
    {
        //boss不断的从redis中获取事件，然后交给workerGroup处理，自己并不做处理
        String key = RedisKeyUtil.getEventQueueKey();
        Thread boss = new Thread(new Runnable()
        {
            @Override
            public void run ()
            {
                while (true)
                {
                    if(!redis.hasKey(key))
                        continue;
                    String event = redis.opsForList().rightPop(key,0,TimeUnit.SECONDS);
                    dispather(event);
                }
            }
        });
        boss.start();
    }

    private void dispather(String event)
    {
        workerGroup.submit(new Worker(event));
    }

    @Override
    public void setApplicationContext (ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    private class Worker implements Runnable
    {
        private String event;
        public Worker(String event)
        {
            this.event=event;
        }

        @Override
        public void run ()
        {
            try
            {
                EventModel eventModel = JSON.parseObject(event, EventModel.class);
                if(config.containsKey(eventModel.getType()))
                {
                    for (EventHandler handler : config.get(eventModel.getType()))
                    {
                        handler.doHandle(eventModel);
                    }
                }
                else
                {
                    logger.error("事件消费者不能识别的事件:"+eventModel.getType());
                }
            }catch (Exception e)
            {
                logger.error(e.getMessage(),e);
            }
        }
    }
}
