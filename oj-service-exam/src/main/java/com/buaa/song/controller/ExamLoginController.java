package com.buaa.song.controller;

import com.buaa.song.result.Result;
import com.buaa.song.service.ExamLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @FileName: ExamLoginController
 * @author: ProgrammerZhao
 * @Date: 2021/4/6
 * @Description:
 */
@RestController
@RequestMapping("/contest")
public class ExamLoginController {

    @Autowired
    private ExamLoginService examLoginService;

    @GetMapping("/login")
    // 这里要requestparam嘛
    public Result login(String username,String password){
        return examLoginService.login(username,password);
    }
}