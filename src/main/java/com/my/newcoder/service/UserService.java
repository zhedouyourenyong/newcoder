package com.my.newcoder.service;

import com.my.newcoder.configuration.Constant;
import com.my.newcoder.dao.LoginTicketDao;
import com.my.newcoder.dao.UserDao;
import com.my.newcoder.model.LoginTicket;
import com.my.newcoder.model.User;
import com.my.newcoder.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService
{
    @Autowired
    private UserDao userDao;
    @Autowired
    private LoginTicketDao loginTicketDao;

    public User getUser (int id)
    {
        return userDao.selectUserById(id);
    }

    //返回值有多种情况，所以用Map更方便
    public Map<String, Object> register (String userName, String password)
    {
        Map<String, Object> resp = new HashMap<>();
        if(StringUtils.isBlank(userName))
        {
            resp.put(Constant.MSG, "用户名不能为空");
        }
        if(StringUtils.isBlank(password))
        {
            resp.put(Constant.MSG, "密码不能为空");
        }
        User user = userDao.selectByName(userName);
        if(user != null)
        {
            resp.put(Constant.MSG, "用户名已经被注册");
            return resp;
        }

        user = new User();
        user.setName(userName);
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        String head = String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000));
        user.setHeadUrl(head);
        user.setPassword(MD5Util.MD5(password + user.getSalt()));
        userDao.addUser(user);


        return resp;
    }


    public Map<String, Object> login (String username, String password)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        if(StringUtils.isBlank(username))
        {
            map.put("msg", "用户名不能为空");
            return map;
        }

        if(StringUtils.isBlank(password))
        {
            map.put("msg", "密码不能为空");
            return map;
        }

        User user = userDao.selectByName(username);
        if(user == null)
        {
            map.put("msg", "用户名不存在");
            return map;
        }

        if(!MD5Util.MD5(password + user.getSalt()).equals(user.getPassword()))
        {
            map.put("msg", "密码不正确");
            return map;
        }

        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        return map;
    }


    private String addLoginTicket (int userId)
    {
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 3600 * 24 * 1);  //单位秒,时间长一点，要大于cookie的有效期
        ticket.setExpired(date);
        ticket.setStatus(0);   // 0：有效  1：无效
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        loginTicketDao.addTicket(ticket);
        return ticket.getTicket();   //只返回唯一标识
    }

    public User selectByName (String name)
    {
        return userDao.selectByName(name);
    }

    public void logout (String ticket)
    {
        loginTicketDao.updateStatus(ticket, 1);
    }
}
