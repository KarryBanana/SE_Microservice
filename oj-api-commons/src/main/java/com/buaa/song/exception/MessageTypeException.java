package com.buaa.song.exception;

public class MessageTypeException extends Exception{

    public MessageTypeException(Integer type) {
        super("消息类型" + type + "不存在");
    }
}
