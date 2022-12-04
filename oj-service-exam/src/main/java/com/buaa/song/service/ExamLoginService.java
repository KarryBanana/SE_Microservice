package com.buaa.song.service;

import com.buaa.song.result.Result;

/**
 * @FileName: ExamLoginService
 * @Author: ProgrammerZhao
 * @Date: 2021/4/6
 * @Description:
 */
public interface ExamLoginService {
    Result login(String username,String password);
}
