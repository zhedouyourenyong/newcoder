package com.my.newcoder.model;

import lombok.Data;

import java.util.Date;

@Data
public class Message
{
    private int id;
    private int fromId;
    private int toId;
    private String content;
    private Date createdDate;
    private int hasRead;
    private String conversationId;
}
