package com.my.newcoder.async.handler;

import com.my.newcoder.async.EventHandler;
import com.my.newcoder.async.EventModel;
import com.my.newcoder.async.EventType;
import com.my.newcoder.model.MailBean;
import com.my.newcoder.service.MailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class LoginExceptionHandler implements EventHandler
{
    @Autowired
    MailSenderService mailSender;

    @Override
    public void doHandle (EventModel model)
    {
        //判断发现这个用户登陆异常
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("username", model.getExt("username"));

        MailBean mailBean=new MailBean();
        mailBean.setRecipient(model.getExt("email"));
        mailBean.setSubject("登陆IP异常");
        mailBean.setTemplate("mails/login_exception.html");
        mailBean.setModel(map);
        mailBean.setContent("test");
        mailSender.sendTemplateMail(mailBean);
    }

    @Override
    public List<EventType> getSupportEventTypes ()
    {
        return Arrays.asList(EventType.LOGIN);
    }
}
