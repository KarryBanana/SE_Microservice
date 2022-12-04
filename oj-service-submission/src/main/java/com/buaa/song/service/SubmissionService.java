package com.buaa.song.service;

import com.alibaba.fastjson.JSONObject;
import com.buaa.song.dto.SubmissionProblemDto;
import com.buaa.song.result.Result;

/**
 * @FileName: SubmissionService
 * @author: ProgrammerZhao
 * @Date: 2021/5/21
 * @Description:
 */

public interface SubmissionService {

    Result list();

    Result findSubmissionById(Integer subId);

    Result submitProblem(Integer userId, SubmissionProblemDto submissionProblemDto);

    Result problemSubList(Integer userId, Integer cid, Integer pid);

    Result updateSubResult(JSONObject data);
}