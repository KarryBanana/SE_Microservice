package com.buaa.song.service.impl;

import com.buaa.song.dao.UserDao;
import com.buaa.song.entity.User;
import com.buaa.song.result.Result;
import com.buaa.song.service.LoginService;
import com.buaa.song.utils.JwtUtil;
import com.buaa.song.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @FileName: LoginServiceImpl
 * @author: ProgrammerZhao
 * @Date: 2020/10/26
 * @Description:
 */

@Service
@RefreshScope
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserDao userDao;

    @Override
    public Result login(String username, String password) {

        try {
            User user = userDao.findByUsername(username);

            if(user == null){
                return Result.fail(403,"用户名错误");
            }else{
                String passwordInSql = user.getPassword();

                if(PasswordUtil.verify(password, passwordInSql)){
                    Date date = new Date(System.currentTimeMillis());
                    user.setLastLogin(date);
                    userDao.save(user);
                    Map<String, Object> claims = new HashMap<>();
                    claims.put("id",user.getId());
                    String token = JwtUtil.encode(username, claims);

                    Map<String, Object> info = new HashMap<>();
                    info.put("token", token); info.put("uid", user.getId());

                    System.out.println("info is " + info);

                    return Result.success("登录成功",info);
                }else{
                    return Result.fail(403,"密码错误");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(500,"系统错误");
        }
    }
}