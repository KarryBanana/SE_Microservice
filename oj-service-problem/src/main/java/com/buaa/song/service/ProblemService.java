package com.buaa.song.service;

import com.buaa.song.dto.PageAndSortDto;
import com.buaa.song.result.Result;

public interface ProblemService {
    // 以下为学生端接口

    Result getPublicProblemList(Integer userId, PageAndSortDto page);

    Result collectProblem(Integer userId, Integer problemId);

    Result showCollectProblem(Integer userId, PageAndSortDto page);

    Result cancelCollectProblem(Integer userId, Integer problemId);

    Result searchProblemById(Integer userId, Integer problemId);

    Result searchProblemByAuthor(Integer userId, String author, PageAndSortDto page);

    Result searchProblemByTitle(Integer userId, String title, PageAndSortDto page);

    Result getAcceptRankList();

    Result getQuestionAnswer();

    Result getLangByProblemId(Integer problemId);

    Result searchProblemByTags(Integer userId, Object content);
}
