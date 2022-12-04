package com.buaa.song.service;


import com.buaa.song.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @FileName: LoginService
 * @Author: ProgrammerZhao
 * @Date: 2020/10/26
 * @Description:
 */

public interface LoginService {

    public Result login(String username, String password);
}
