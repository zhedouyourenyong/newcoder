package com.my.newcoder.service;

import com.my.newcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


@Service
public class LikeService
{
    @Autowired
    StringRedisTemplate redis;

    public long getLikeCount (int entityType, int entityId)
    {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        return redis.opsForSet().size(likeKey);
    }

    public int getLikeStatus (int userId, int entityType, int entityId)
    {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        if(redis.opsForSet().isMember(likeKey, String.valueOf(userId)))
        {
            return 1;
        }
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        return redis.opsForSet().isMember(disLikeKey, String.valueOf(userId)) ? -1 : 0;
    }

    public long like(int userId, int entityType, int entityId)
    {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        redis.opsForSet().add(likeKey, String.valueOf(userId));

        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        redis.opsForSet().remove(disLikeKey, String.valueOf(userId));
        return redis.opsForSet().size(likeKey);
    }

    public long disLike (int userId, int entityType, int entityId)
    {
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        redis.opsForSet().add(disLikeKey, String.valueOf(userId));

        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        redis.opsForSet().remove(likeKey, String.valueOf(userId));

        return redis.opsForSet().size(likeKey);
    }
}
