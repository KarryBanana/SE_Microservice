package com.buaa.song.service;

import com.buaa.song.dto.ApplyClassDto;
import com.buaa.song.dto.PageAndSortDto;
import com.buaa.song.entity.Class;
import com.buaa.song.result.Result;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @FileName: ClassService
 * @Author: ProgrammerZhao
 * @Date: 2020/11/7
 * @Description:
 */

public interface ClassService {

    // 以下为用户端接口
    Result isClassMember(Integer userId, Integer classId);

    Result getClassInfo(Integer classId);

    Result findClassById(Integer classId);

    Result getClassList(Integer userId);

    Result getClassCourse(Integer classId);

    Result getClassMember(Integer classId);

    Result getClassProblemList(Integer userId, Integer classId, PageAndSortDto page);

    Result getClassProblemPageNum(Integer userId, Integer classId, Integer limit);

    Result getClassExam(Integer userId, Integer classId, PageAndSortDto page);

    Result getClassExamPageNum(Integer userId, Integer classId, Integer limit);

    Result applyClass(Integer userId, ApplyClassDto applyClassDto);

    Result problemList(Integer classId,Integer userId,PageAndSortDto page);

    Result getClassContest(Integer classId, PageAndSortDto page);
}
