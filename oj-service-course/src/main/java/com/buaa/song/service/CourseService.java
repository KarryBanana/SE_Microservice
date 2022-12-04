package com.buaa.song.service;

import com.buaa.song.entity.Course;
import com.buaa.song.result.Result;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @FileName: CourseService
 * @Author: ProgrammerZhao
 * @Date: 2021/2/19
 * @Description:
 */
public interface CourseService {

    // 获取课程列表
    Result getCourseList(Integer userId);

    Result getCourseName();
    
}
