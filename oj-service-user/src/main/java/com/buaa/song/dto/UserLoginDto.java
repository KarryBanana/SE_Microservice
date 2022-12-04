package com.buaa.song.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @FileName: UserLoginDto
 * @author: ProgrammerZhao
 * @Date: 2021/8/5
 * @Description:
 */
@Data
@NoArgsConstructor
public class UserLoginDto {
    private String username;
    private String password;
}