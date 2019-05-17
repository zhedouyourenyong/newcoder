package com.my.newcoder.dao;


import com.my.newcoder.model.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MessageDao
{
    String TABLE_NAME = " message ";
    String INSERT_FIELDS = " from_id, to_id, content, has_read, conversation_id, created_date ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;


    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS, ") values (#{fromId},#{toId},#{content},#{hasRead},#{conversationId},#{createdDate})"})
    int addMessage (Message message);


    @Select({"select count(id) from ", TABLE_NAME, " where has_read=0 and conversation_id=#{conversationId} and to_id=#{userId}"})
    int getConvesationUnreadCount (@Param("userId") int userId, @Param("conversationId") String conversationId);


    @Select({"select ",SELECT_FIELDS," from",TABLE_NAME," where conversation_id=#{conversationId} order by id desc limit #{offset}, #{limit}"})
    List<Message> getConversationDetail(@Param("conversationId") String conversationId, @Param("offset") int offset, @Param("limit") int limit);

    @Select({"SELECT ",INSERT_FIELDS,", COUNT(id) AS id FROM (SELECT * FROM ",TABLE_NAME," WHERE to_id=#{userId} OR from_id=#{userId} ORDER BY id DESC) AS tmp GROUP BY conversation_id ORDER BY created_date DESC LIMIT #{offset}, #{limit}"})
    List<Message> getConversationList(@Param("userId") int userId,@Param("offset") int offset, @Param("limit") int limit);
}