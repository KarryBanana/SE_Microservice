package com.buaa.song.exception;

/**
 * @FileName: UserNotFindException
 * @author: ProgrammerZhao
 * @Date: 2020/11/24
 * @Description:
 */

public class UserNotFindException extends Exception {

    public UserNotFindException(Integer id) {
        super("用户"+id+"不存在");
    }
}