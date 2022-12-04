package com.buaa.song.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @FileName: VerificateCodeDto
 * @author: ProgrammerZhao
 * @Date: 2021/8/4
 * @Description:
 */
@Data
@NoArgsConstructor
public class VerificateCodeDto {
    private Integer id;
    private String email;
    private String code;
}