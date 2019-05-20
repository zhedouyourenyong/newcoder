package com.my.newcoder.async.handler;

import com.my.newcoder.async.EventHandler;
import com.my.newcoder.async.EventModel;
import com.my.newcoder.async.EventType;
import com.my.newcoder.model.Message;
import com.my.newcoder.model.User;
import com.my.newcoder.service.MessageService;
import com.my.newcoder.service.UserService;
import com.my.newcoder.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Component
public class LikeHandler implements EventHandler
{
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;

    @Override
    public void doHandle (EventModel event)
    {
        Message message = new Message();
        message.setFromId(JsonUtil.SYSTEM_USERID);
        message.setToId(event.getEntityOwnerId());
        message.setCreatedDate(new Date());
        User user = userService.getUser(event.getActorId());
        message.setContent("用户" + user.getName() + "赞了你的评论,http://127.0.0.1:8080/question/" + event.getExt("questionId"));
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes ()
    {
        return Arrays.asList(EventType.LIKE);
    }
}
