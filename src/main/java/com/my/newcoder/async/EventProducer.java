package com.my.newcoder.async;


import com.alibaba.fastjson.JSON;
import com.my.newcoder.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;


/*
* 同一发送事件到redis
* 队列也可以用BlockingQueue
* 但redis可以被共享
* */
@Component
public class EventProducer
{
    private Logger logger= LoggerFactory.getLogger(EventProducer.class);

    @Autowired
    StringRedisTemplate redis;

    public boolean fireEvent(EventModel event)
    {
        try
        {
            String json= JSON.toJSONString(event);
            String key = RedisKeyUtil.getEventQueueKey();
            redis.opsForList().rightPush(key,json);
            return true;
        }catch (Exception e)
        {
            logger.error("异步事件发送失败",e.getMessage());
            return false;
        }
    }

}
