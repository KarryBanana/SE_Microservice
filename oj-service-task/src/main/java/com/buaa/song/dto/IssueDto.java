package com.buaa.song.dto;

import lombok.Data;

/**
 * @FileName: IssueDto
 * @author: ProgrammerZhao
 * @Date: 2021/5/22
 * @Description:
 */
@Data
public class IssueDto {

    private Integer order;
    private String title;
    private String content;
}