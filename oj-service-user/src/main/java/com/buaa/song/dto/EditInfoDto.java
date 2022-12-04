package com.buaa.song.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @FileName: EditInfoDto
 * @author: ProgrammerZhao
 * @Date: 2021/8/5
 * @Description:
 */
@Data
@NoArgsConstructor
public class EditInfoDto {

    private String nickname;
    private String description;
    private String school;
    private String major;
    private String studentId;
    private String realname;
}