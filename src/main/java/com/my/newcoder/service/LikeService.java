package com.my.newcoder.service;

import com.my.newcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


@Service
public class LikeService
{
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public long getLikeCount (int entityType, int entityId)
    {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        return stringRedisTemplate.opsForSet().size(likeKey);
    }

    public int getLikeStatus (int userId, int entityType, int entityId)
    {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        if(stringRedisTemplate.opsForSet().isMember(likeKey, String.valueOf(userId)))
        {
            return 1;
        }
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        return stringRedisTemplate.opsForSet().isMember(disLikeKey, String.valueOf(userId)) ? -1 : 0;
    }

    public long like(int userId, int entityType, int entityId)
    {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        stringRedisTemplate.opsForSet().add(likeKey, String.valueOf(userId));

        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        stringRedisTemplate.opsForSet().remove(disLikeKey, String.valueOf(userId));
        return stringRedisTemplate.opsForSet().size(likeKey);
    }

    public long disLike (int userId, int entityType, int entityId)
    {
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        stringRedisTemplate.opsForSet().add(disLikeKey, String.valueOf(userId));

        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        stringRedisTemplate.opsForSet().remove(likeKey, String.valueOf(userId));

        return stringRedisTemplate.opsForSet().size(likeKey);
    }
}
