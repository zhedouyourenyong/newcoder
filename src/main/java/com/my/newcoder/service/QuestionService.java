package com.my.newcoder.service;


import com.my.newcoder.dao.QuestionDao;
import com.my.newcoder.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class QuestionService
{
    @Autowired
    QuestionDao questionDao;
    @Autowired
    SensitiveService sensitiveService;

    public List<Question> getLatestQuestions (int offset, int limit)
    {
        return questionDao.selectLatestQuestions(offset, limit);
    }


    public List<Question> getLatestQuestionsByuerId (int userId, int offset, int limit)
    {
        return questionDao.selectLatestQuestionsByuserId(userId, offset, limit);
    }


    public int addQuestion (Question question)
    {
        //敏感词和脚本过滤
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        question.setTitle(sensitiveService.filter(question.getTitle()));
        question.setContent(sensitiveService.filter(question.getContent()));

        return questionDao.addQuestion(question) > 0 ? question.getId() : 0;

    }

    public Question getById (int id)
    {
        return questionDao.getById(id);
    }

    public int updateCommentCount (int id, int count)
    {
        return questionDao.updateCommentCount(id, count);
    }
}
