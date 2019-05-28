package com.my.newcoder.util;


import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.MultiPartEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.StringWriter;
import java.util.Map;


@Service
public class MailSender
{
    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);
    private MultiPartEmail  email;

    @Autowired
    private Configuration freemarkCfg;

    @PostConstruct
    private void init()
    {
        email = new MultiPartEmail();
        email.setHostName("smtp.163.com");
        email.setSmtpPort(25);
        email.setSSLOnConnect(false);
        email.setAuthenticator(new DefaultAuthenticator("pyzzh96@163.com", "flzx3qc"));
    }

    public boolean sendWithHTMLTemplate (String to, String subject, String template, Map<String, Object> model)
    {
        try
        {
            Template tp = freemarkCfg.getTemplate(template);
            StringWriter result=new StringWriter();
            tp.process(model,result);
            email.setFrom("pyzzh96@163.com"); // 发送者
            email.setSubject(subject); // 邮件标题
            email.setMsg(result.toString()); // 邮件内容
            email.addTo(to); // 接收者 ( 允许有多个 )
            email.send();  //耗时长
            return true;
        } catch (Exception e)
        {
            logger.error("发送邮件失败" + e.getMessage());
            return false;
        }
    }

}
