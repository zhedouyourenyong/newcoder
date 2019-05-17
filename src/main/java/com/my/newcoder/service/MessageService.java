package com.my.newcoder.service;


import com.my.newcoder.dao.MessageDao;
import com.my.newcoder.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService
{
    @Autowired
    MessageDao messageDao;
    @Autowired
    SensitiveService sensitiveService;

    public int addMessage (Message message)
    {
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveService.filter(message.getContent()));
        return messageDao.addMessage(message);
    }

    public List<Message> getConversationDetail (String conversationId, int offset, int limit)
    {
        return messageDao.getConversationDetail(conversationId, offset, limit);
    }

    public List<Message> getConversationList (int userId, int offset, int limit)
    {
        return messageDao.getConversationList(userId, offset, limit);
    }

    public int getConvesationUnreadCount (int userId, String conversationId)
    {
        return messageDao.getConvesationUnreadCount(userId, conversationId);
    }
}
