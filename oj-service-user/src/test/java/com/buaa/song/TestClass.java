package com.buaa.song;

import com.buaa.song.dao.UserDao;
import com.buaa.song.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @FileName: TestClass
 * @author: ProgrammerZhao
 * @Date: 2021/5/22
 * @Description:
 */
@SpringBootTest
public class TestClass {

    @Autowired
    private UserDao userDao;

    @Test
    public void test(){
        String username = "test1@test.com'#";
        String password = "123456";
        User user = userDao.findUser(username, password);
        System.out.println(user);
    }
}