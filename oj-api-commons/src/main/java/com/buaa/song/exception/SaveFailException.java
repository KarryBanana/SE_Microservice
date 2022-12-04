package com.buaa.song.exception;

/**
 * @FileName: SaveFailException
 * @author: ProgrammerZhao
 * @Date: 2021/5/19
 * @Description:
 */

public class SaveFailException extends Exception {
    public SaveFailException(String msg){
        super(msg);
    }
}