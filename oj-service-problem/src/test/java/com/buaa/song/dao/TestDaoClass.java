package com.buaa.song.dao;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.swing.text.StyledEditorKit.ForegroundAction;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @FileName: TestDaoClass
 * @author: ProgrammerZhao
 * @Date: 2021/3/24
 * @Description:
 */
@SpringBootTest
public class TestDaoClass {

    @Autowired
    private ProblemDao problemDao;

    @Test
    public void test(){

    }

    @Test
    public void test2(){
//        ArrayList<Integer> userIds = new ArrayList<>();
//        userIds.add(1);
//        userIds.add(2);
//
//        Integer result = problemDao.insertIntoProblemShare(2, 1, 2, new Date(System.currentTimeMillis()));
//        System.out.println(result);
    }
}
