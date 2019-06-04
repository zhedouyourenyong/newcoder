package com.my.newcoder.model;


import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import java.util.Date;

@Data
public class Feed
{
    private int id;
    private int type;
    private int userId;
    private Date createdDate;
    private String data;
    private JSONObject dataJSON = null;

    public void setData (String data)
    {
        this.data = data;
        dataJSON = JSONObject.parseObject(data);
    }

    public String get (String key)
    {
        return dataJSON == null ? null : dataJSON.getString(key);
    }
}
