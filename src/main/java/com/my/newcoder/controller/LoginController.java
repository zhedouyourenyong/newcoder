package com.my.newcoder.controller;


import com.my.newcoder.async.EventModel;
import com.my.newcoder.async.EventProducer;
import com.my.newcoder.async.EventType;
import com.my.newcoder.configuration.Constant;
import com.my.newcoder.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class LoginController
{
    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;
    @Autowired
    EventProducer eventProducer;

    //用户注册
    @RequestMapping(path = {"/reg/"}, method = {RequestMethod.POST})
    public String reg (Model model, @RequestParam("username") String username, @RequestParam("password") String password)
    {
        try
        {
            Map<String, Object> resp = userService.register(username, password);
            if(resp.containsKey(Constant.MSG))
            {
                model.addAttribute(Constant.MSG, resp.get(Constant.MSG));
                return "login";
            }
            return "redirect:/";
        } catch (Exception e)
        {
            logger.error("注册异常", e.getMessage());
            return "login";
        }
    }

    //注册并登陆
    @RequestMapping(path = {"/reglogin"}, method = {RequestMethod.GET})
    public String regloginPage (Model model, @RequestParam(value = "next", required = false) String next)
    {
        model.addAttribute("next", next);
        return "login";
    }


    @RequestMapping(path = {"/login/"}, method = {RequestMethod.POST})
    public String login (Model model, @RequestParam("username") String username, @RequestParam("password") String password,
                         @RequestParam(value = "next", required = false) String next,
                         @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme,
                         HttpServletResponse response)
    {
        try
        {
            Map<String, Object> map = userService.login(username, password);
            if(map.containsKey("ticket"))
            {
                Cookie cookie = new Cookie(Constant.TICKET, map.get(Constant.TICKET).toString());
                cookie.setPath("/");
                if(rememberme)
                {
                    cookie.setMaxAge(3600 * 6);  //有效期六个小时
                }

                //假设这里是登录异常
//                EventModel test=new EventModel(EventType.LOGIN)
//                        .setExt("username", username)
//                        .setExt("email", "1124212685@qq.com")
//                        .setActorId((int)map.get("userId"));
//                eventProducer.fireEvent(test);

                response.addCookie(cookie);
                if(StringUtils.isNotBlank(next))
                {
                    return "redirect:" + next;
                }
                return "redirect:/";
            } else
            {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
        } catch (Exception e)
        {
            logger.error("登陆异常" + e.getMessage());
            return "login";
        }
    }

    @RequestMapping(path = {"/logout"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String logout (@CookieValue("ticket") String ticket)
    {
        userService.logout(ticket);
        return "redirect:/";
    }

}
