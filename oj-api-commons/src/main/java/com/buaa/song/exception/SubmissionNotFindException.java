package com.buaa.song.exception;

public class SubmissionNotFindException extends Exception{
    public SubmissionNotFindException(Integer subId) {
        super("提交ID" + subId + "不存在");
    }
}
