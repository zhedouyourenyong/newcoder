package com.my.newcoder.controller;


import com.my.newcoder.model.*;
import com.my.newcoder.service.CommentService;
import com.my.newcoder.service.FollowService;
import com.my.newcoder.service.QuestionService;
import com.my.newcoder.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;


@Controller
public class IndexController
{
    private static Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;
    @Autowired
    FollowService followService;
    @Autowired
    CommentService commentService;
    @Autowired
    HostHolder hostHolder;


    @RequestMapping(path = "/")
    public String index (Model model)
    {
        model.addAttribute("vos", getQuestions(0, 0, 10));
        return "index";
    }

    @RequestMapping(path = {"/user/{userId}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userIndex (Model model, @PathVariable("userId") int userId)
    {
        model.addAttribute("vos", getQuestions(userId, 0, 10));

        User user = userService.getUser(userId);
        ViewObject vo = new ViewObject();
        vo.set("user", user);
        vo.set("commentCount", commentService.getUserCommentCount(userId));
        vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        vo.set("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
        if(hostHolder.getUser() != null)
        {
            vo.set("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId));
        } else
        {
            vo.set("followed", false);
        }
        model.addAttribute("profileUser", vo);
        return "profile";
    }

    private List<ViewObject> getQuestions (int userId, int offset, int limit)
    {
        List<Question> questionList = null;
        if(userId != 0)
            questionList = questionService.getLatestQuestionsByuerId(userId, offset, limit);
        else
            questionList = questionService.getLatestQuestions(offset, limit);

        List<ViewObject> vos = new ArrayList<>();
        for (Question question : questionList)
        {
            ViewObject vo = new ViewObject();
            vo.set("question", question);
            vo.set("user", userService.getUser(question.getUserId()));
            vos.add(vo);
        }
        return vos;
    }
}
