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
import java.util.concurrent.TimeUnit;

@Component
public class EventConsumer implements InitializingBean, ApplicationContextAware
{
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private Map<EventType, List<EventHandler>> config = new HashMap<>();
    private ApplicationContext applicationContext;

    @Autowired
    StringRedisTemplate redis;


    public void init ()
    {
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

        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run ()
            {
                while (true)
                {
                    String key = RedisKeyUtil.getEventQueueKey();
                    if(!redis.hasKey(key))
                        continue;
                    String events = redis.opsForList().rightPop(key,0,TimeUnit.SECONDS);

                    //改成线程池？？
                    EventModel eventModel = JSON.parseObject(events, EventModel.class);
                    if(!config.containsKey(eventModel.getType()))
                    {
                        logger.error("事件消费者不能识别的事件:"+eventModel.getType());
                        continue;
                    }

                    for (EventHandler handler : config.get(eventModel.getType()))
                    {
                        handler.doHandle(eventModel);
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void afterPropertiesSet () throws Exception
    {
        init();
    }

    @Override
    public void setApplicationContext (ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }
}
