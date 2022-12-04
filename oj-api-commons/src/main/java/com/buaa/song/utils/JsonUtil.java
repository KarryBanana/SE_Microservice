package com.buaa.song.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @FileName: JsonUtil
 * @author: ProgrammerZhao
 * @Date: 2020/12/15
 * @Description:
 */

public class JsonUtil {

    public static String getJsonString(Object obj){
        String jsonString = JSON.toJSONString(obj);
        return jsonString;
    }

    public static Object getObjectFromJson(String jsonString){
        Object obj = JSONObject.parse(jsonString);
        return obj;
    }
}