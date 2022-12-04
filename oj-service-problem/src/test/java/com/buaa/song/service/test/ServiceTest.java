package com.buaa.song.service.test;

import com.buaa.song.result.Result;
import com.buaa.song.service.ProblemAdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

/**
 * @FileName: ServiceTest
 * @author: ProgrammerZhao
 * @Date: 2021/3/25
 * @Description:
 */
@SpringBootTest
public class ServiceTest {

    @Autowired
    private ProblemAdminService problemAdminService;

    @Test
    public void test(){
        Result result = problemAdminService.searchProblem(1, 1, "零", null);
        List<Map<String,Object>> data = (List<Map<String, Object>>) result.getData();
        for(Map<String,Object> p : data){
            for(String key : p.keySet()){
                System.out.println(key + "  " + p.get(key));
            }
        }
    }
}