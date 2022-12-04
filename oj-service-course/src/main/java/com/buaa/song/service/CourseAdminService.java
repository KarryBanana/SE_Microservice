package com.buaa.song.service;

import com.buaa.song.entity.Course;
import com.buaa.song.result.Result;

public interface CourseAdminService {
    Result createCourse(Integer userId, Course c);
}
