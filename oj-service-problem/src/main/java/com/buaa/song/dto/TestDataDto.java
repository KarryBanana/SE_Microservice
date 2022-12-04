package com.buaa.song.dto;

import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

/**
 * @FileName: TestDataDto
 * @author: ProgrammerZhao
 * @Date: 2020/11/16
 * @Description:
 */

@Data
@ToString
public class TestDataDto {

    private MultipartFile input;
    private MultipartFile output;
    private Double weight;

}