package com.buaa.song.dto;

import lombok.Data;
import lombok.ToString;

/**
 * @FileName: ExamLoginDto
 * @author: ProgrammerZhao
 * @Date: 2021/4/7
 * @Description:
 */
@Data
@ToString
public class ExamLoginDto {
    private Integer examId;
    private String token;
}