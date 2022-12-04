package com.buaa.song.service.impl;

import com.buaa.song.dao.CourseDao;
import com.buaa.song.entity.Course;
import com.buaa.song.entity.User;
import com.buaa.song.exception.UserNotFindException;
import com.buaa.song.result.Result;
import com.buaa.song.service.CourseService;
import com.buaa.song.utils.RestUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @FileName: CourseServiceImpl
 * @author: ProgrammerZhao
 * @Date: 2021/2/19
 * @Description:
 */
@Service
@RefreshScope
@Slf4j
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseDao courseDao;

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(CourseServiceImpl.class);
    private static final String userServiceUrl = "http://oj-service-user";

    public Result getCourseList(Integer userId) {
        List<Map<String, Object>> courseList = new LinkedList<>();
        // 如果CourseDao是PagingAndSortingRepository，要提供PageRequest才能findAll()

        /*  一次性取回所有的该用户可看到的班级数据
            一条数据组成：班级id 班级name 班级在课程中的顺序order 课程id 课程name 课程描述
         */
        try {

            Result result = checkUserExist(userId);
            User user = (User) result.getData();
            // 普通用户
            if(user.getRole().equals(2)) {

                List<Map<String, Object>> classData = courseDao.getUserClass(userId);

                int len = classData.size();

                Map<String, Object> course = new HashMap<String, Object>();
                List<Map<String, Object>> classList = new LinkedList<>();

                int i = 0;
                while( i < len ) {
                    String id = classData.get(i).get("id").toString();
                    String name = (String) classData.get(i).get("name");
                    classList.add(new HashMap<String, Object>() {{
                        put("id", id);
                        put("name", name);
                    }});
                    if(i + 1 < len) {
                        int courseId = (Integer) classData.get(i).get("courseId");
                        int nextCourseId = (Integer) classData.get(i + 1).get("courseId");
                        if(courseId != nextCourseId) {
                            course.put("id", courseId);
                            course.put("name", classData.get(i).get("courseName"));
                            course.put("info", classData.get(i).get("description"));
                            course.put("classList", classList);

                            courseList.add(course);
                            course = new HashMap<String, Object>();
                            classList = new LinkedList<>();
                        }
                    }
                    i++;
                }
                // 把剩下班级的加上
                course.put("id", classData.get(i - 1).get("courseId"));
                course.put("name", classData.get(i - 1).get("courseName"));
                course.put("info", classData.get(i - 1).get("description"));
                course.put("classList", classList);
                courseList.add(course);

            } else { // 超级管理员可以看所有的课程与班级
                List<Course> courses = courseDao.findAll();

                for(Course course : courses) {
                    Map<String, Object> course_elem = new HashMap<>();
                    course_elem.put("id", course.getId());
                    course_elem.put("name", course.getName());
                    course_elem.put("info", course.getDescription());
                    Integer courseId = course.getId();
                    List<Map<String, Object>> classList = courseDao.getCourseClass(courseId);
                    course_elem.put("classList", classList);

                    courseList.add(course_elem);
                }
            }
        } catch (UserNotFindException e) {
            logger.error(e.getMessage());
            return Result.fail(400, e.getMessage());
        }

        return Result.success(courseList);
    }

    @Override
    public Result getCourseName() {
        List<Map<String, Object>> courses = courseDao.findAllCourseName();
        return Result.success(courses);
    }


    private Result checkUserExist(Integer userId) throws UserNotFindException {
        Result result = RestUtil.get(restTemplate, userServiceUrl + "/user/" + userId, User.class);
        if (!result.getStatus().equals(200)) {
            throw new UserNotFindException(userId);
        }
        return result;
    }
    
}