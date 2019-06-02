package com.my.newcoder.model;


import lombok.Data;
import java.io.File;
import java.io.Serializable;
import java.util.Map;

@Data
public class MailBean implements Serializable
{
    private static final long serialVersionUID = -2116367492649751914L;
    private String recipient;//邮件接收人
    private String subject; //邮件主题
    private String content; //邮件内容

    //freemark使用
    private String template;
    private Map<String, Object> model ;

    //附件
    private String attachmentFilename;
    private File attachmentFile;
}
