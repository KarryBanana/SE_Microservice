package com.buaa.song.service;

import com.buaa.song.dto.*;
import com.buaa.song.result.Result;
import org.springframework.stereotype.Service;

/**
 * @FileName: UserService
 * @author: ProgrammerZhao
 * @Date: 2020/10/24
 * @Description:
 */

@Service
public interface UserService {

    Result register(RegisterDto dto);

    Result editVisit(Integer userId, Integer privacy);

    Result isUsernameExist(String username);

    Result isSuperAdmin(Integer userId);

    Result findUserById(Integer id);

    Result getUserPrivacy(Integer id);

    Result resetPassword(String token, ResetPasswordDto dto);

    Result importUserByExcel(String studentId, String name, String suffix);

    Result login(UserLoginDto dto);

    Result sendCode(String email);

    Result verificateCode(VerificateCodeDto dto);

    Result logout(String token);

    Result getInfo(Integer userId);

    Result editInfo(Integer userId, EditInfoDto dto);

    Result updatePassword(Integer userId, UpdatePasswordDto dto);

    Result getAnnualSubmission(Integer userId);

    Result getAllSubmissionType(Integer userId);

    Result getWeekSubmission(Integer userId);

    Result getLatestSubmission(Integer userId);

    Result getUserSchools();
}