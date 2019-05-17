package com.my.newcoder.model;

import lombok.Data;

import java.util.Date;


@Data
public class LoginTicket
{
    private int id;
    private int userId;
    private Date expired;
    private int status;// 0有效，1无效
    private String ticket;
}
