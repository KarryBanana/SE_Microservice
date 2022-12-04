package com.buaa.song.service.impl;

import com.buaa.song.dao.TempPasswordDao;
import com.buaa.song.dto.ExamLoginDto;
import com.buaa.song.entity.TempPassword;
import com.buaa.song.entity.User;
import com.buaa.song.result.Result;
import com.buaa.song.service.ExamLoginService;
import com.buaa.song.utils.JwtUtil;
import com.buaa.song.utils.RestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @FileName: ExamLoginServiceImpl
 * @author: ProgrammerZhao
 * @Date: 2021/4/6
 * @Description:
 */
@Service
public class ExamLoginServiceImpl implements ExamLoginService {

    private static final String userServiceUrl = "http://oj-service-user";

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private TempPasswordDao tempPasswordDao;

    @Override
    public Result login(String username, String password) {
        Map<String, Object> params = new HashMap();
        params.put("username", username);
        Result result = RestUtil.get(restTemplate, userServiceUrl + "/isUsernameExist", params, User.class);
        if (result.getStatus().equals(404)) {
            return Result.fail(404, "该用户名不存在");
        }
        User user = (User) result.getData();
        Integer userId = user.getId();
        TempPassword tempPassword = tempPasswordDao.findByUserIdAndTempPassword(userId, password);
        if (tempPassword != null && tempPassword.getIsAvailable().equals(1)) {
            ExamLoginDto examLoginDto = new ExamLoginDto();
            examLoginDto.setExamId(tempPassword.getExamId());
            Map<String, Object> claims = new HashMap<>();
            claims.put("user_id", userId);
            claims.put("exam_id",tempPassword.getExamId());
            examLoginDto.setToken(JwtUtil.encode(username, claims));

            tempPassword.setIsAvailable(0);
            tempPasswordDao.save(tempPassword);

            return Result.success(examLoginDto);
        } else if (tempPassword == null) {
            return Result.fail(400, "用户无权限考试，或者临时密码错误");
        } else {
            return Result.fail(400, "临时密码已失效");
        }
    }
}