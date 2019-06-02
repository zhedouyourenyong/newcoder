package com.my.newcoder;

import com.my.newcoder.model.MailBean;
import com.my.newcoder.service.MailSenderService;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.Date;


@RunWith(SpringRunner.class)
@SpringBootTest
public class NewcoderApplicationTests
{
    @Autowired
    StringRedisTemplate redis;
    @Autowired
    MailSenderService mailSenderService;

    @Test
    public void contextLoads ()
    {
        MailBean mailBean = new MailBean();
        mailBean.setRecipient("1124212685@qq.com");
        mailBean.setSubject("SpringBootMail之这是一封文本的邮件");
        mailBean.setContent("SpringBootMail发送一个简单格式的邮件，时间为：" +(new Date()));
        mailBean.setAttachmentFile(new File("src/main/resources/static/images/img/mail.png"));
        mailSenderService.sendSimpleMail(mailBean);
//        mailSenderService.sendAttachmentMail(mailBean);
    }

}
