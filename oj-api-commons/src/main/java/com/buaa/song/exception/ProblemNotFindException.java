package com.buaa.song.exception;

/**
 * @FileName: ProblemNotFindException
 * @author: ProgrammerZhao
 * @Date: 2020/11/25
 * @Description:
 */

public class ProblemNotFindException extends Exception {

    public ProblemNotFindException(Integer id){
        super("题目"+id+"不存在");
    }
}