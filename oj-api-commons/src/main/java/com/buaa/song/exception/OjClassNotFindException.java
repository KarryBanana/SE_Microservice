package com.buaa.song.exception;

public class OjClassNotFindException extends Exception {
    public OjClassNotFindException(Integer id) {
        super("班级"+id+"不存在");
    }
}
