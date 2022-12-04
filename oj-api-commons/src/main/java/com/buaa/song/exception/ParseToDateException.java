package com.buaa.song.exception;

public class ParseToDateException extends Exception{
    public ParseToDateException(String s) {
        super("字符串" + s + "无法转换成Date类型");
    }
}
