package com.buaa.song.dto;

import lombok.Data;

/**
 * @FileName: UpdatePasswordDto
 * @author: ProgrammerZhao
 * @Date: 2021/8/5
 * @Description:
 */
@Data
public class UpdatePasswordDto {
    private String oldPassword;
    private String newPassword;
}