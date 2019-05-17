package com.my.newcoder.dao;

import com.my.newcoder.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserDao
{
    String TABLE_NAME=" user ";
    String INSERT_FILEDS=" name,password,salt,headUrl ";

    @Insert({"insert into ",TABLE_NAME,"(",INSERT_FILEDS,")","values (#{name},#{password},#{salt},#{headUrl})"})
    int addUser(User user);

    @Select({"select *"," from ",TABLE_NAME," where id = #{id}"})
    User selectUserById(int id);

    @Delete({"delete from ",TABLE_NAME," where id = #{id}"})
    void deleteUesrById(int id);

    @Update({"update ",TABLE_NAME," set password = #{password} where id = #{id}"})
    void updatePassword(User user);

    @Select({"select * from ",TABLE_NAME," where name= #{userName}"})
    User selectByName(String userName);
}
