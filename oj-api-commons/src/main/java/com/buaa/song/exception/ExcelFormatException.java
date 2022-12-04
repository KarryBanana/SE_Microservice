package com.buaa.song.exception;

import java.io.IOException;

/**
 * @FileName: ExcelFormatException
 * @author: ProgrammerZhao
 * @Date: 2020/11/10
 * @Description:
 */

public class ExcelFormatException extends IOException {

    public ExcelFormatException(String message){
        super(message);
    }
}