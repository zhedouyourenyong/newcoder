package com.my.newcoder.controller;

import com.my.newcoder.async.EventModel;
import com.my.newcoder.async.EventProducer;
import com.my.newcoder.async.EventType;
import com.my.newcoder.model.Comment;
import com.my.newcoder.model.EntityType;
import com.my.newcoder.model.HostHolder;
import com.my.newcoder.service.CommentService;
import com.my.newcoder.service.QuestionService;
import com.my.newcoder.service.SensitiveService;
import com.my.newcoder.service.UserService;
import com.my.newcoder.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
public class CommentController
{
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    HostHolder hostHolder;
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;
    @Autowired
    QuestionService questionService;
    @Autowired
    SensitiveService sensitiveService;
    @Autowired
    EventProducer eventProducer;

    //问题评论的接口
    @RequestMapping(path = {"/addComment"}, method = {RequestMethod.POST})
    public String addComment (@RequestParam("questionId") int questionId, @RequestParam("content") String content)
    {
        //关注评论是怎么和所属的用户和所属的问题关联的
        try
        {
            Comment comment = new Comment();
            if(hostHolder.getUser() != null)
            {
                comment.setUserId(hostHolder.getUser().getId());
            } else
            {
                comment.setUserId(JsonUtil.ANONYMOUS_USERID);
            }
            comment.setContent(content);
            comment.setEntityId(questionId);
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            comment.setCreatedDate(new Date());
            comment.setStatus(0);
            commentService.addComment(comment);

            // 更新题目里的评论数量,应该为异步处理
            int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
            questionService.updateCommentCount(comment.getEntityId(), count);

            eventProducer.fireEvent(new EventModel(EventType.COMMENT).setActorId(comment.getUserId())
                    .setEntityId(questionId));

        } catch (Exception e)
        {
            logger.error("增加评论失败" + e.getMessage());
        }

        //增加完评论后，跳转到该问题的详情页面
        return "redirect:/question/" + String.valueOf(questionId);
    }
}
