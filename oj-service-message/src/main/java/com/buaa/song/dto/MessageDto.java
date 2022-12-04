package com.buaa.song.dto;

import com.buaa.song.entity.Message;
import com.buaa.song.exception.MessageTypeException;
import com.buaa.song.exception.ParseToDateException;
import lombok.Data;
import lombok.ToString;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@ToString
public class MessageDto {

    protected Integer type;
    protected String title;
    protected String content;
    protected Integer sender;
    protected Integer broadcast;
    protected Integer classId;
    protected Integer userId;
    protected String timing; // 预定的通知发送时间

    public Message transferToMsg() throws MessageTypeException, ParseToDateException{
        Message msg = new Message();

        assignAttribute(msg);

        // 设置创建 修改时间
        Date date = new Date(System.currentTimeMillis());
        msg.setCreateTime(date);
        msg.setUpdateTime(date);

        return msg;
    }

    public void updateMsg(Message msg) throws MessageTypeException, ParseToDateException{
        assignAttribute(msg);

        Date date = new Date(System.currentTimeMillis());
        msg.setUpdateTime(date);
    }

    public void assignAttribute(Message msg) throws MessageTypeException, ParseToDateException{
        msg.setType(type);
        msg.setTitle(title);
        msg.setContent(content);
        msg.setSender(sender);

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date sendTime = dateFormat.parse(timing);
            msg.setTiming(sendTime);
        }catch (ParseException e) {
            throw new ParseToDateException(timing);
        }

        // 设置消息类型
        msg.setBroadcast(0); msg.setClassId(0); msg.setUserId(0);
        if(type.equals(0)) {
            msg.setBroadcast(broadcast);
        } else if(type.equals(1)) {
            msg.setClassId(classId);
        } else if(type.equals(2)) {
            msg.setUserId(userId);
        } else { // 针对非0, 1, 2的处理
            throw new MessageTypeException(type);
        }
    }
}
