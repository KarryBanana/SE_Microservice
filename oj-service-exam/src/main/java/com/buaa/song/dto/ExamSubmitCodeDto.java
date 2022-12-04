package com.buaa.song.dto;

import lombok.Data;

/**
 * @FileName: ExamSubmitCodeDto
 * @author: ProgrammerZhao
 * @Date: 2021/7/26
 * @Description:
 */
@Data
public class ExamSubmitCodeDto {
    private Integer problemId;
    private Integer language;
    private String code;
}