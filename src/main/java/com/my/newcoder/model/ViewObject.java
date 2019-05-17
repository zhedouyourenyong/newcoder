package com.my.newcoder.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/*
*一个ViewObject代表一个要展示的内容，
* 比如问题，除了问题内容本身还要展示发布问题的用户的相关信息，有两大部分组成
* */
@Data
public class ViewObject
{
    private Map<String, Object> objs = new HashMap<String, Object>();

    public void set (String key, Object value)
    {
        objs.put(key, value);
    }

    public Object get (String key)
    {
        return objs.get(key);
    }
}
