package com.my.newcoder.controller;

import com.my.newcoder.async.EventModel;
import com.my.newcoder.async.EventProducer;
import com.my.newcoder.async.EventType;
import com.my.newcoder.model.Comment;
import com.my.newcoder.model.EntityType;
import com.my.newcoder.model.HostHolder;
import com.my.newcoder.service.CommentService;
import com.my.newcoder.service.LikeService;
import com.my.newcoder.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LikeController
{
    private static Logger logger = LoggerFactory.getLogger(LikeController.class);

    @Autowired
    LikeService likeService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;
    @Autowired
    EventProducer eventProducer;


    @RequestMapping(path = {"/like"}, method = {RequestMethod.POST})
    @ResponseBody
    public String like (@RequestParam("commentId") int commentId)
    {
        if(hostHolder.getUser() == null)
        {
            return JsonUtil.getJSONString(999);
        }

        Comment comment = commentService.getCommentById(commentId);

        eventProducer.fireEvent(new EventModel(EventType.LIKE)
                .setActorId(hostHolder.getUser().getId()).setEntityId(commentId)
                .setEntityType(EntityType.ENTITY_COMMENT).setEntityOwnerId(comment.getUserId())
                .setExt("questionId", String.valueOf(comment.getEntityId())));

        long likeCount = likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
        return JsonUtil.getJSONString(0, String.valueOf(likeCount));
    }

    @RequestMapping(path = {"/dislike"}, method = {RequestMethod.POST})
    @ResponseBody
    public String dislike (@RequestParam("commentId") int commentId)
    {
        if(hostHolder.getUser() == null)
        {
            return JsonUtil.getJSONString(999);
        }

        long likeCount = likeService.disLike(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
        return JsonUtil.getJSONString(0, String.valueOf(likeCount));
    }
}
