package com.my.newcoder.interceptor;

import com.my.newcoder.dao.LoginTicketDao;
import com.my.newcoder.dao.UserDao;
import com.my.newcoder.model.HostHolder;
import com.my.newcoder.model.LoginTicket;
import com.my.newcoder.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;


@Component
public class PassportInterceptor implements HandlerInterceptor
{

    @Autowired
    private LoginTicketDao loginTicketDAO;
    @Autowired
    private UserDao userDAO;
    @Autowired
    private HostHolder hostHolder;


    //每次访问时，判断用户端是否有ticket，有代表是以登录的用户
    @Override
    public boolean preHandle (HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception
    {
        String ticket = null;
        if(httpServletRequest.getCookies() != null)
        {
            for (Cookie cookie : httpServletRequest.getCookies())
            {
                if(cookie.getName().equals("ticket"))
                {
                    ticket = cookie.getValue();
                    break;
                }
            }
        }

        if(ticket != null)
        {
            LoginTicket loginTicket = loginTicketDAO.selectByTicket(ticket);
            if(loginTicket == null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus() != 0)
            {
                return true;
            }

            User user = userDAO.selectUserById(loginTicket.getUserId());
            hostHolder.setUser(user);
        }

        //不能中断请求的响应，返回false代表结束整个请求，用户不会得到任何响应
        return true;
    }

    //渲染页面之前，如果用户有登录就把user加入模型
    @Override
    public void postHandle (HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception
    {
        if(modelAndView != null && hostHolder.getUser() != null)
        {
            modelAndView.addObject("user", hostHolder.getUser());
        }
    }

    //每次请求回应后都要把 threadLocal中对应的user删了，代表一次请求的结束
    //如果不删threadLocal中的user会越来越多，因为每次响应请求的线程时不同的
    @Override
    public void afterCompletion (HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception
    {
        hostHolder.clear();
    }
}
