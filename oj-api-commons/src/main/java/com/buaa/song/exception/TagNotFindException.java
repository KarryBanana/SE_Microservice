package com.buaa.song.exception;


/**
 * @FileName: TagNotFindException
 * @author: ProgrammerZhao
 * @Date: 2020/11/21
 * @Description:
 */

public class TagNotFindException extends Exception {

    public TagNotFindException(Integer id){
        super("ID为"+id+"的标签不存在");
    }
}