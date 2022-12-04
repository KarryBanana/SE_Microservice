package com.buaa.song.dto;

import com.buaa.song.utils.model.ExcelWriteModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @FileName: ImportUserDto
 * @author: ProgrammerZhao
 * @Date: 2021/3/7
 * @Description:
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ImportUserDto {

    private Integer userId;

    private ExcelWriteModel model;
}