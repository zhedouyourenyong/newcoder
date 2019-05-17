package com.my.newcoder.controller;


import com.my.newcoder.dao.QuestionDao;
import com.my.newcoder.dao.UserDao;
import com.my.newcoder.model.Question;
import com.my.newcoder.model.ViewObject;
import com.my.newcoder.service.QuestionService;
import com.my.newcoder.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;


@Controller
public class IndexController
{
    private  static Logger logger= LoggerFactory.getLogger(IndexController.class);

    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;

//    首页
    @RequestMapping(path = "/")
    public String index(Model model)
    {
        model.addAttribute("vos",getQuestions(0,0,10));
        return "index";
    }

    private List<ViewObject> getQuestions(int userId, int offset, int limit)
    {
        List<Question> questionList=null;
        if(userId!=0)
            questionList = questionService.getLatestQuestionsByuerId(userId, offset, limit);
        else
            questionList=questionService.getLatestQuestions(offset, limit);

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
