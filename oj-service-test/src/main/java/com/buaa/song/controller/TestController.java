package com.buaa.song.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.buaa.song.entity.Authentication;
import com.buaa.song.entity.Tag;
import com.buaa.song.result.Result;
import com.buaa.song.result.Result2;
import com.buaa.song.utils.RestUtil;
import org.apache.poi.ss.formula.functions.T;
import org.aspectj.bridge.MessageWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @FileName: TestController
 * @author: ProgrammerZhao
 * @Date: 2021/2/21
 * @Description:
 */
@RestController
public class TestController {

    @Autowired
    private RestTemplate restTemplate;

    private static final String userServiceUrl = "http://localhost:8810/";

//    @GetMapping("/test")
//    public Result test(){
//        String url = userServiceUrl + "/user/auth?studentId=" + "00000001rtet";
//        Result result = restTemplate.getForObject(url, Result.class);
//        System.out.println(result);
//        Authentication authentication = BeanUtil.fillBeanWithMap((Map) result.getData(), new Authentication(), false);
//        System.out.println(authentication);
//        return result;
//    }
//    @GetMapping("/test2")
//    public Result test2(){
//        String url = userServiceUrl + "/user/auth/4";
//        Result result = restTemplate.getForObject(url, Result.class);
//        Object data = result.getData();
//        Authentication authentication = BeanUtil.fillBeanWithMap((Map) data, new Authentication(), false);
//        System.out.println(authentication);
//        return Result.success();
//    }

    @GetMapping("/rest/get1")
    public Result test1(@RequestParam Integer id, @RequestParam String name){
        Tag tag = new Tag();
        tag.setId(id);
        tag.setName(name);
        return Result.success("test1",tag);
    }

    @GetMapping("rest/get2")
    public Result test2(Tag tag){
        System.out.println(tag);
        return Result.success("test1",tag);
    }

    @PostMapping("/rest/get3")
    public Result test3(@RequestBody Tag tag){
        System.out.println(tag);
        return Result.success(tag);
    }

    @GetMapping("/test")
    public void restTest(){
        String url = "http://oj-service-test/rest/get1";
        HashMap<String, Object> map = new HashMap<>();
        map.put("id",1);
        map.put("name","tag");
        Result result = RestUtil.get(restTemplate, url, map, Tag.class);
        Tag tag = (Tag) result.getData();
        System.out.println(tag);
    }

}