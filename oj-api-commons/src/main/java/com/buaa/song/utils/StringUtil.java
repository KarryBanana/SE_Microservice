package com.buaa.song.utils;

import org.elasticsearch.action.admin.cluster.node.tasks.get.GetTaskAction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @FileName: StringUtil
 * @author: ProgrammerZhao
 * @Date: 2021/3/7
 * @Description:
 */

public class StringUtil {

    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyz";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(26);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

}