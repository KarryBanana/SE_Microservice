package com.buaa.song.controller;

import com.buaa.song.entity.Course;
import com.buaa.song.result.Result;
import com.buaa.song.service.CourseAdminService;
import com.buaa.song.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.buaa.song.utils.RequestUtil.getUserIdFromRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @FileName: CourseController
 * @author: ProgrammerZhao
 * @Date: 2021/2/19
 * @Description:
 */

@RestController
@RequestMapping("/admin/course")
@CrossOrigin
public class CourseAdminController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseAdminService courseAdminService;


    @GetMapping("/name")
    public Result getCourseName() {
        return courseService.getCourseName();
    }

    @PostMapping("/instance")
    public Result createCourse(HttpServletRequest request, @RequestBody Course c) {
        return courseAdminService.createCourse(getUserIdFromRequest(request), c);
    }

}