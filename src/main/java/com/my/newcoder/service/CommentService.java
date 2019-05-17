package com.my.newcoder.service;


import com.my.newcoder.dao.CommentDao;
import com.my.newcoder.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService
{
    @Autowired
    CommentDao commentDao;
    @Autowired
    SensitiveService sensitiveService;

    public List<Comment> getCommentsByEntity (int entityId, int entityType)
    {
        return commentDao.selectByEntity(entityId, entityType);
    }

    public int addComment (Comment comment)
    {
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveService.filter(comment.getContent()));
        return commentDao.addComment(comment);
    }

    public int getCommentCount (int entityId, int entityType)
    {
        return commentDao.getCommentCount(entityId, entityType);
    }

    public void deleteComment (int entityId, int entityType)
    {
        commentDao.updateStatus(entityId, entityType, 1);
    }
}
