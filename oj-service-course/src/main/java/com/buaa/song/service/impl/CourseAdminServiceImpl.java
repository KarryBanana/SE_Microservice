package com.buaa.song.service.impl;

import com.buaa.song.dao.CourseDao;
import com.buaa.song.entity.Course;
import com.buaa.song.entity.User;
import com.buaa.song.exception.UserNotFindException;
import com.buaa.song.result.Result;
import com.buaa.song.service.CourseAdminService;
import com.buaa.song.utils.RestUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Slf4j
@RefreshScope
public class CourseAdminServiceImpl implements CourseAdminService {
    private static final Logger logger = LoggerFactory.getLogger(CourseAdminServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CourseDao courseDao;

    private static final String userServiceUrl = "http://oj-service-user";


    @Override

    public Result createCourse(Integer userId, @RequestBody Course c) {
        try{
            Result result = checkUserExist(userId);
            User user = (User) result.getData();
            if(user.getRole().equals(3)){
                c.setCreateTime(new Date());
                courseDao.save(c);

                return Result.success(c);
            } else {
                return Result.fail(400, "用户无权进行操作");
            }

        } catch (UserNotFindException e){
            logger.error(e.getMessage());
            return Result.fail(400, e.getMessage());
        }
    }

    private Result checkUserExist(Integer userId) throws UserNotFindException {
        Result result = RestUtil.get(restTemplate, userServiceUrl + "/user/" + userId, User.class);
        if (!result.getStatus().equals(200)) {
            throw new UserNotFindException(userId);
        }
        return result;
    }
}
