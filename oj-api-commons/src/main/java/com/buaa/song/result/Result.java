package com.buaa.song.result;

import lombok.Data;
import lombok.ToString;

import javax.persistence.criteria.CriteriaBuilder.In;

/**
 * @FileName: Result
 * @author: ProgrammerZhao
 * @Date: 2020/10/26
 * @Description:对返回结果的封装
 */
@Data
@ToString
public class Result {

    Integer status;
    String msg;
    Object data;

    public Result(){}

    private Result(Integer status){
        this.status = status;
    }

    private Result(Integer status,String msg){
        this.status = status;
        this.msg = msg;
    }

    private Result(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public static Result success(){
        return new Result(200);
    }

    public static Result success(String msg){
        return new Result(200,msg);
    }

    public static Result success(Object data) {
        return new Result(200,null,data);
    }

    public static Result success(String msg,Object data){
        return new Result(200,msg,data);
    }

    public static Result fail(Integer status){
        return new Result(status);
    }

    public static Result fail(Integer status, String msg){
        return new Result(status,msg);
    }
}