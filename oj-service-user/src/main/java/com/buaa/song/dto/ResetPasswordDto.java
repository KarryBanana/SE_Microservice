package com.buaa.song.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @FileName: ResetPasswordDto
 * @author: ProgrammerZhao
 * @Date: 2021/8/4
 * @Description:
 */
@Data
@NoArgsConstructor
public class ResetPasswordDto {
    private Integer id;
    private String password;
}