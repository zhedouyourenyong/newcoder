package com.my.newcoder.dao;


import com.my.newcoder.model.Question;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface QuestionDao
{
    String TABLE_NAME = " question ";
    String INSERT_FIELDS = " title, content, created_date, user_id, comment_count ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS, ") values (#{title},#{content},#{createdDate},#{userId},#{commentCount})"})
    int addQuestion (Question question);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    Question getById (int id);

    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," ORDER BY id DESC LIMIT #{offset},#{limit}"})
    List<Question> selectLatestQuestions (@Param("offset") int offset, @Param("limit") int limit);

    @Update({"update ", TABLE_NAME, " set comment_count = #{commentCount} where id=#{id}"})
    int updateCommentCount (@Param("id") int id, @Param("commentCount") int commentCount);


    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where user_id= #{userId} ORDER BY id DESC LIMIT #{offset},#{limit}"})
    List<Question> selectLatestQuestionsByuserId (@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);
}
