package com.buaa.song.exception;

/**
 * @program: ExamNotFindException
 * @description: TODO
 * @author: Karry
 * @create: 2022-11-30 10:43
 **/
public class ExamNotFindException extends Exception{
    public ExamNotFindException(Integer examId) {
        super("比赛"+examId+"不存在");
    }
}
