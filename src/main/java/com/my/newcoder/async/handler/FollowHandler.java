package com.my.newcoder.async.handler;


import com.my.newcoder.async.EventHandler;
import com.my.newcoder.async.EventModel;
import com.my.newcoder.async.EventType;
import com.my.newcoder.model.EntityType;
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
public class FollowHandler implements EventHandler
{
    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHandle (EventModel model)
    {
        Message message = new Message();
        message.setFromId(JsonUtil.SYSTEM_USERID);
        message.setToId(model.getEntityOwnerId());
        message.setCreatedDate(new Date());
        User user = userService.getUser(model.getActorId());

        if(model.getEntityType() == EntityType.ENTITY_QUESTION)
        {
            message.setContent("用户" + user.getName()
                    + "关注了你的问题,http://127.0.0.1:8080/question/" + model.getEntityId());
        } else if(model.getEntityType() == EntityType.ENTITY_USER)
        {
            message.setContent("用户" + user.getName()
                    + "关注了你,http://127.0.0.1:8080/user/" + model.getActorId());
        }

        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes ()
    {
        return Arrays.asList(EventType.FOLLOW);
    }
}
