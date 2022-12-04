package com.buaa.song.exception;

/**
 * @FileName: LanguageNotFindException
 * @author: ProgrammerZhao
 * @Date: 2020/12/15
 * @Description:
 */

public class LanguageNotFindException extends Exception {

    public LanguageNotFindException(Integer lid){
        super("语言"+lid+"不存在");
    }

}