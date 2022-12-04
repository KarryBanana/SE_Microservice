package com.buaa.song.utils.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @FileName: ExcelModel
 * @author: ProgrammerZhao
 * @Date: 2020/11/9
 * @Description:
 */
@Data
@ToString
public class ExcelReadModel {

    @ExcelProperty("学号")
    private String studentId;

    @ExcelProperty("姓名")
    private String name;
}