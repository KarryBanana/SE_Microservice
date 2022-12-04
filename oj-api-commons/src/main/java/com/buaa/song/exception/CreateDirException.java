package com.buaa.song.exception;

/**
 * @FileName: CreateDirException
 * @author: ProgrammerZhao
 * @Date: 2020/12/15
 * @Description:
 */

public class CreateDirException extends Exception{

    public CreateDirException(String path){
        super("创建 "+path+" 目录异常");
    }

}