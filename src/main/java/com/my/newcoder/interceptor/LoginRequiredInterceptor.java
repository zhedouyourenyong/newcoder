package com.my.newcoder.interceptor;

import com.my.newcoder.model.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
* 拦截需要登录才能访问的页面
* */
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor
{

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle (HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception
    {
        //如果没登录，就跳转到的登录页面，并将目标页面作为参数放到登录页面中，方便登录完后直接跳转到目标页面
        if(hostHolder.getUser() == null)
        {
            httpServletResponse.sendRedirect("/reglogin?next=" + httpServletRequest.getRequestURI());
            return false;
        }
        return true;
    }

    @Override
    public void postHandle (HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception
    {
    }

    @Override
    public void afterCompletion (HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception
    {
    }
}
