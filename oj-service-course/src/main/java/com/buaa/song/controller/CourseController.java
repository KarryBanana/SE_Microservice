package com.buaa.song.controller;

import com.buaa.song.result.Result;
import com.buaa.song.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import static com.buaa.song.utils.RequestUtil.getUserIdFromRequest;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/list")
    public Result getCourseList(HttpServletRequest request) {
        return courseService.getCourseList(getUserIdFromRequest(request));
    }
}
