package com.buaa.song.exception;

public class MessageNotFindException extends Exception{

    public MessageNotFindException(Integer id) {
        super("消息"+id+"不存在");
    }
}
