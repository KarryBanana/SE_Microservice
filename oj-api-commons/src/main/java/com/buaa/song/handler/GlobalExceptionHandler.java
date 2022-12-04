package com.buaa.song.handler;

import com.buaa.song.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @FileName: GlobalExceptionHandler
 * @author: ProgrammerZhao
 * @Date: 2021/11/7
 * @Description:
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseBody
    public Result exceptionHandler(HttpServletRequest request, Exception e){
        log.error("发生错误："+e.getMessage());
        e.printStackTrace();
        return Result.fail(500,"unknown error");
    }
}
