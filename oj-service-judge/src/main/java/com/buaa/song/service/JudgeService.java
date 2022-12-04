package com.buaa.song.service;

import com.alibaba.fastjson.JSONObject;
import com.buaa.song.result.Result;

import javax.servlet.http.HttpServletResponse;

public interface JudgeService {

    void downloadFile(Integer problemId, HttpServletResponse response);

    Result getJudgeTask();
}
