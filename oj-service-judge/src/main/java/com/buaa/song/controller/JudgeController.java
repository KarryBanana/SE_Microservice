package com.buaa.song.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.buaa.song.result.Result;
import com.buaa.song.service.JudgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/judge")
@CrossOrigin(origins = {"*"})
public class JudgeController {

    @Autowired
    private JudgeService judgeService;

    @GetMapping("/task/test")
    public Result askTask() {
        JSONObject taskInfo = JSON.parseObject("{\n" +
                "\t\"submission_id\": \"test\",\n" +
                "    \t\"question_id\": \"test\",\n" +
                "    \t\"question_date\": \"114-05-14_15-15-15\",\n" +
                "   \t \"submit_time\": \"1453-05-29_07-01-34\",\n" +
                "    \t\"language\": \"c\",\n" +
                "    \t\"content\": \"\" \n" +
                "}");
        return Result.success(taskInfo);
    }

    @GetMapping("/file/{id}")
    public void downloadFile(@PathVariable("id") Integer problemId, HttpServletResponse response) {
        judgeService.downloadFile(problemId, response);
    }

    @GetMapping("/task")
    public Result getJudgeTask(){
        return judgeService.getJudgeTask();
    }
}
