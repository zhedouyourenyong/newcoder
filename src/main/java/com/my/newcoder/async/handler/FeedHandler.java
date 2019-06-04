package com.my.newcoder.async.handler;

import com.alibaba.fastjson.JSONObject;
import com.my.newcoder.async.EventHandler;
import com.my.newcoder.async.EventModel;
import com.my.newcoder.async.EventType;
import com.my.newcoder.model.EntityType;
import com.my.newcoder.model.Feed;
import com.my.newcoder.model.Question;
import com.my.newcoder.model.User;
import com.my.newcoder.service.FeedService;
import com.my.newcoder.service.FollowService;
import com.my.newcoder.service.QuestionService;
import com.my.newcoder.service.UserService;
import com.my.newcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class FeedHandler implements EventHandler
{
    @Autowired
    FollowService followService;
    @Autowired
    UserService userService;
    @Autowired
    FeedService feedService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    QuestionService questionService;

    @Override
    public void doHandle (EventModel model)
    {
        // 为了测试，把model的userId随机一下
        Random r = new Random();
        model.setActorId(15 + r.nextInt(10));

        // 构造一个新鲜事
        Feed feed = new Feed();
        feed.setCreatedDate(new Date());
        feed.setType(model.getType().getValue());
        feed.setUserId(model.getActorId());
        feed.setData(buildFeedData(model));
        if(feed.getData() == null)
        {
            // 不支持的feed
            return;
        }
        feedService.addFeed(feed);

        // 获得所有粉丝
        List<Integer> followers = followService.getFollowers(EntityType.ENTITY_USER, model.getActorId(), Integer.MAX_VALUE);
        // 系统队列
        followers.add(0);
        // 给所有粉丝推事件
        for (int follower : followers)
        {
            String timelineKey = RedisKeyUtil.getTimelineKey(follower);
            stringRedisTemplate.opsForList().leftPush(timelineKey, String.valueOf(feed.getId()));
            // 限制最长长度，如果timelineKey的长度过大，就删除后面的新鲜事
        }
    }

    @Override
    public List<EventType> getSupportEventTypes ()
    {
        return Arrays.asList(new EventType[]{EventType.COMMENT, EventType.FOLLOW});
    }

    private String buildFeedData (EventModel model)
    {
        Map<String, String> map = new HashMap<>();
        // 触发用户是通用的
        User actor = userService.getUser(model.getActorId());
        if(actor == null)
        {
            return null;
        }
        map.put("userId", String.valueOf(actor.getId()));
        map.put("userHead", actor.getHeadUrl());
        map.put("userName", actor.getName());

        if(model.getType() == EventType.COMMENT ||
                (model.getType() == EventType.FOLLOW && model.getEntityType() == EntityType.ENTITY_QUESTION))
        {
            Question question = questionService.getById(model.getEntityId());
            if(question == null)
            {
                return null;
            }
            map.put("questionId", String.valueOf(question.getId()));
            map.put("questionTitle", question.getTitle());
            return JSONObject.toJSONString(map);
        }
        return null;
    }
}
