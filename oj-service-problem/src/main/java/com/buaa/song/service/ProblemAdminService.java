package com.buaa.song.service;

import com.buaa.song.dto.PageAndSortDto;
import com.buaa.song.dto.ProblemDto;
import com.buaa.song.dto.ProblemUpdateDto;
import com.buaa.song.result.Result;


import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @FileName: ProblemService
 * @Author: ProgrammerZhao
 * @Date: 2020/11/16
 * @Description:
 */
public interface ProblemAdminService {

    Result createProblem(Integer userId, ProblemDto problemDto);

    Result getProblemList(Integer userId, PageAndSortDto page);

    Result getPageNumber(Integer userIdFromRequest, PageAndSortDto page);

    Result getProblemInfo(Integer problemId);

    Result searchProblem(Integer userId, Integer searchWay, String value, PageAndSortDto page);

    Result getProblemEdit(Integer problemId);

    Result updateProblem(Integer problemId, ProblemUpdateDto updateDto);

    Result deleteProblem(Integer problemId);

    String download(Integer problemId, HttpServletResponse response);

    Result shareProblem(Integer problemId, List<Integer> userIds, Integer userIdFromRequest);

    Result addProblemToCourse(Integer problemId, List<Integer> courseIds, Integer userIdFromRequest);

    Result problemListForTask(Integer courseId);

}
