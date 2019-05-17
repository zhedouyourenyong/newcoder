package com.my.newcoder;


import com.my.newcoder.dao.QuestionDao;
import com.my.newcoder.dao.UserDao;
import com.my.newcoder.model.Question;
import com.my.newcoder.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Random;


@RunWith(SpringRunner.class)
@SpringBootTest
public class NewcoderApplicationTests
{
//    @Autowired
//    StringRedisTemplate redis;

    @Test
    public void contextLoads ()
    {
    }

}
