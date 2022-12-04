package com.buaa.song.service;

import com.buaa.song.dto.ExamDto;
import com.buaa.song.dto.PageAndSortDto;
import com.buaa.song.result.Result;

/**
 * @FileName: ExamService
 * @author: ProgrammerZhao
 * @Date: 2021/4/7
 * @Description:
 */

public interface ExamService {

    Result getInfo(Integer examId);

    Result getProblems(Integer examId);

    Result getIssues(Integer examId);

    Result getQuestion(Integer examId);

    Result getRank(Integer examId);

    Result getSubmission(Integer examId, Integer userIdFromRequest);

    Result askQuestion(String content, Integer examId, Integer problemId, Integer userIdFromRequest);

    Result submit(Integer userIdFromRequest, Integer examId, Integer problem, Integer language, String code);

    Result getCode(Integer id);

    Result list(Integer userId, PageAndSortDto page);

    Result getListPageNum(Integer userId, Integer limit);

    Result getClassExam(Integer userId, Integer classId, PageAndSortDto page);

    Result createExam(ExamDto examDto, Integer userId);

    Result getExamEdit(Integer examId);

    Result editExam(ExamDto examDto, Integer userId);

    Result deleteExam(Integer examId);

    Result getAllSubmission();

    // 设置比赛权限
    Result setExamAccess(Integer classId, Integer examId, Integer access);

    void test();
}
