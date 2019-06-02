package com.my.newcoder.service;


import com.my.newcoder.model.MailBean;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.MultiPartEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;


@Service
public class MailSenderService
{
    private static final Logger logger = LoggerFactory.getLogger(MailSenderService.class);

    @Value("${spring.mail.username}")
    private String MAIL_SENDER; //邮件发送者
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    Configuration configuration;
    @Resource(name = "cachedThreadPool")
    private ExecutorService threadPool;


    //简单的文本邮件
    public void sendSimpleMail (MailBean mailBean)
    {
        threadPool.execute(new Runnable()
        {
            @Override
            public void run ()
            {
                try
                {
                    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
                    simpleMailMessage.setFrom(MAIL_SENDER);
                    simpleMailMessage.setTo(mailBean.getRecipient());
                    simpleMailMessage.setSubject(mailBean.getSubject());
                    simpleMailMessage.setText(mailBean.getContent());
                    javaMailSender.send(simpleMailMessage);
                } catch (Exception e)
                {
                    logger.error("文本邮件发送失败", e.getMessage());
                }
            }
        });
    }

    //发送基于freemark的邮件
    public void sendTemplateMail (MailBean mailBean)
    {
        threadPool.execute(new Runnable()
        {
            @Override
            public void run ()
            {
                try
                {
                    MimeMessage mimeMailMessage = javaMailSender.createMimeMessage();
                    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMailMessage, true);
                    mimeMessageHelper.setFrom(MAIL_SENDER);
                    mimeMessageHelper.setTo(mailBean.getRecipient());
                    mimeMessageHelper.setSubject(mailBean.getSubject());

                    String template=mailBean.getTemplate();
                    Map<String,Object> model=mailBean.getModel();
                    Template mailTemplate = configuration.getTemplate(template);
                    String text = FreeMarkerTemplateUtils.processTemplateIntoString(mailTemplate, model);
                    mimeMessageHelper.setText(text, true);

                    javaMailSender.send(mimeMailMessage);
                } catch (Exception e)
                {
                    logger.error("freemark邮件发送失败", e.getMessage());
                }
            }
        });
    }

    //发送携带附件的邮件     失败，还不能用
    public void sendAttachmentMail (MailBean mailBean)
    {
        threadPool.execute(new Runnable()
        {
            @Override
            public void run ()
            {
                try
                {
                    MimeMessage mimeMailMessage = javaMailSender.createMimeMessage();
                    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMailMessage, true);
                    mimeMessageHelper.setFrom(MAIL_SENDER);
                    mimeMessageHelper.setTo(mailBean.getRecipient());
                    mimeMessageHelper.setSubject(mailBean.getSubject());
                    mimeMessageHelper.setText(mailBean.getContent());

                    FileSystemResource file=new FileSystemResource(mailBean.getAttachmentFile());
                    mimeMessageHelper.addAttachment(mailBean.getAttachmentFilename(), file);

                    javaMailSender.send(mimeMailMessage);
                } catch (Exception e)
                {
                    logger.error("附件邮件发送失败", e.getMessage());
                }
            }
        });
    }

}
