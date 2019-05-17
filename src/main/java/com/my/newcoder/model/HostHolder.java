package com.my.newcoder.model;

import org.springframework.stereotype.Component;

/*
* 单机可以用ThreadLocal，如果是集群，要把这里换成可共享的
* */
@Component
public class HostHolder
{
    private static ThreadLocal<User> users = new ThreadLocal<User>();

    public User getUser ()
    {
        return users.get();
    }

    public void setUser (User user)
    {
        users.set(user);
    }

    public void clear ()
    {
        users.remove();
    }
}
