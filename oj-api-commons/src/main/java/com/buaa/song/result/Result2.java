package com.buaa.song.result;

import com.buaa.song.entity.User;
import lombok.Data;
import lombok.ToString;

/**
 * @FileName: Result2
 * @author: ProgrammerZhao
 * @Date: 2021/2/21
 * @Description:
 */
@Data
@ToString
public class Result2<T> {

    private Integer status;
    private T data;

}