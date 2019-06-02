package com.my.newcoder.controller;

import com.my.newcoder.model.*;
import com.my.newcoder.service.*;
import com.my.newcoder.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Controller
public class QuestionController
{
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    QuestionService questionService;
    @Autowired
    CommentService commentService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    UserService userService;
    @Autowired
    LikeService likeService;
    @Autowired
    FollowService followService;

    //查看问题的细节
    @RequestMapping(value = "/question/{qid}", method = {RequestMethod.GET})
    public String questionDetail (Model model, @PathVariable("qid") int qid)
    {
        Question question = questionService.getById(qid);
        model.addAttribute("question", question);
        List<Comment> commentList = commentService.getCommentsByEntity(qid, EntityType.ENTITY_QUESTION);
        List<ViewObject> vos = new ArrayList<>();
        for (Comment comment : commentList)
        {
            ViewObject vo = new ViewObject();
            vo.set("comment", comment);
            if(hostHolder.getUser() == null)
            {
                vo.set("liked", 0);
            } else
            {
                vo.set("liked", likeService.getLikeStatus(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, comment.getId()));
            }
            vo.set("likeCount", likeService.getLikeCount(EntityType.ENTITY_COMMENT, comment.getId()));
            vo.set("user", userService.getUser(comment.getUserId()));
            vos.add(vo);
        }
        model.addAttribute("comments", vos);

        List<ViewObject> followUsers = new ArrayList<ViewObject>();
        // 获取关注的用户信息
        List<Integer> users = followService.getFollowers(EntityType.ENTITY_QUESTION, qid, 20);
        for (Integer userId : users)
        {
            ViewObject vo = new ViewObject();
            User u = userService.getUser(userId);
            if(u == null)
            {
                continue;
            }
            vo.set("name", u.getName());
            vo.set("headUrl", u.getHeadUrl());
            vo.set("id", u.getId());
            followUsers.add(vo);
        }
        model.addAttribute("followUsers", followUsers);
        if(hostHolder.getUser() != null)
        {
            model.addAttribute("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, qid));
        } else
        {
            model.addAttribute("followed", false);
        }
        return "detail";
    }


    @RequestMapping(value = "/question/add", method = {RequestMethod.POST})
    @ResponseBody
    public String addQuestion (@RequestParam("title") String title, @RequestParam("content") String content)
    {
        try
        {
            Question question = new Question();
            question.setContent(content);
            question.setCreatedDate(new Date());
            question.setTitle(title);
            if(hostHolder.getUser() == null)
            {
                question.setUserId(JsonUtil.ANONYMOUS_USERID);  //匿名用户
            } else
            {
                question.setUserId(hostHolder.getUser().getId());
            }

            if(questionService.addQuestion(question) > 0)
            {
                return JsonUtil.getJSONString(0);
            }
        } catch (Exception e)
        {
            logger.error("增加题目失败" + e.getMessage());
        }
        return JsonUtil.getJSONString(1, "失败");
    }

}
