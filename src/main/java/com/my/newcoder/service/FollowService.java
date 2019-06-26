package com.my.newcoder.service;


import com.my.newcoder.util.RedisKeyUtil;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class FollowService
{
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //  用户关注了某个实体,可以关注问题,关注用户,关注评论等任何实体
    public boolean follow (int userId, int entityType, int entityId)
    {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);  //跟随者
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);   //关注者

        Date date = new Date();
        stringRedisTemplate.setEnableTransactionSupport(true);
        stringRedisTemplate.multi();
        stringRedisTemplate.opsForZSet().add(followerKey, String.valueOf(userId), date.getTime());  //粉丝列表
        stringRedisTemplate.opsForZSet().add(followeeKey, String.valueOf(entityId), date.getTime()); //我的关注列表
        List<Object> result = stringRedisTemplate.exec();

        return checkTransactionResult(result);
    }

    //  取消关注
    public boolean unfollow (int userId, int entityType, int entityId)
    {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);

        stringRedisTemplate.setEnableTransactionSupport(true);
        stringRedisTemplate.multi();
        stringRedisTemplate.opsForZSet().remove(followerKey, String.valueOf(userId));  //粉丝列表
        stringRedisTemplate.opsForZSet().remove(followeeKey, String.valueOf(entityId)); //我的关注列表
        List<Object> result = stringRedisTemplate.exec();

        return checkTransactionResult(result);
    }

    private boolean checkTransactionResult(List<Object> result)
    {
        if(result==null||result.size()==0)
            return false;

        int flag=1;
        if(result.get(0) instanceof Long)
            flag=0;

        for(Object object:result)
        {
            if(flag==1&&(Boolean)object==false)
                return false;
            else if(flag==0&&(Long)object!=1)
                return false;

        }
        return true;
    }


    //  判断用户是否关注了某个实体
    public boolean isFollower (int userId, int entityType, int entityId)
    {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return stringRedisTemplate.opsForZSet().score(followerKey, String.valueOf(userId)) != null;
    }

    //获取所有粉丝
    public List<Integer> getFollowers (int entityType, int entityId, int count)
    {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        Set<String> myFollowers = stringRedisTemplate.opsForZSet().range(followerKey, 0, count);
        return getIdsFromSet(myFollowers);
    }

    //获取粉丝，分页
    public List<Integer> getFollowers (int entityType, int entityId, int offset, int count)
    {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        Set<String> myFollowers = stringRedisTemplate.opsForZSet().range(followerKey, offset, count);
        return getIdsFromSet(myFollowers);
    }

    //获取所有关注者
    public List<Integer> getFollowees (int userId, int entityType, int count)
    {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Set<String> myFollowees = stringRedisTemplate.opsForZSet().range(followeeKey, 0, count);
        return getIdsFromSet(myFollowees);
    }

    //获取关注者，分页
    public List<Integer> getFollowees (int userId, int entityType, int offset, int count)
    {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Set<String> myFollowees = stringRedisTemplate.opsForZSet().range(followeeKey, offset, count);
        return getIdsFromSet(myFollowees);
    }

    //获取粉丝数量
    public long getFollowerCount (int entityType, int entityId)
    {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return stringRedisTemplate.opsForZSet().zCard(followerKey);
    }

    //获取关注者的数量
    public long getFolloweeCount (int userId, int entityType)
    {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return stringRedisTemplate.opsForZSet().zCard(followeeKey);
    }

    //将集合set转化为list
    private List<Integer> getIdsFromSet (Set<String> idset)
    {
        List<Integer> ids = new ArrayList<>();
        for (String str : idset)
        {
            ids.add(Integer.parseInt(str));
        }
        return ids;
    }
}
