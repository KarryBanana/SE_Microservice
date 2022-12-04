package com.buaa.song.dto;

import com.buaa.song.entity.User;
import com.buaa.song.utils.PasswordUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @FileName: RegisterDto
 * @author: ProgrammerZhao
 * @Date: 2021/8/4
 * @Description:
 */
@Data
@NoArgsConstructor
public class RegisterDto {
    private String username;
    private String password;
    private String nickname;
    private String description;
    private String school;
    private String major;
    private String studentId;
    private String realName;

    public User transferToUser() {
        User user = new User();
        user.setUsername(username)
                .setPassword(PasswordUtil.generate(password))
                .setNickname(nickname)
                .setDescription(description)
                .setSchool(school)
                .setMajor(major)
                .setStudentId(studentId)
                .setRealName(realName);
        return user;
    }
}