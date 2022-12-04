package com.buaa.song.controller;

import com.buaa.song.result.Result;
import com.buaa.song.service.UserService;
import com.buaa.song.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @FileName: UserAdminController
 * @author: ProgrammerZhao
 * @Date: 2021/10/28
 * @Description:
 */
@RestController
@RequestMapping("/admin")
@CrossOrigin
public class UserAdminController {
    @Autowired
    private UserService userService;

    @PostMapping("/user/excel/import")
    public Result importUserByExcel(@RequestParam String studentId, @RequestParam String name, @RequestParam String suffix){
        return userService.importUserByExcel(studentId,name,suffix);
    }

    private static Integer getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        return (Integer) JwtUtil.decode(token).get("id");
    }
}