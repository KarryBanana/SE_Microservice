package com.buaa.song.dto;

import com.buaa.song.entity.Message;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class MessageUpdateDto { // 和前端交流后，修改通知消息只修改标题或者内容
    protected String title;
    protected String content;

    public void updateMsg(Message msg) {
        msg.setTitle(title);
        msg.setContent(content);

        Date date = new Date(System.currentTimeMillis());
        msg.setUpdateTime(date);
    }
}
