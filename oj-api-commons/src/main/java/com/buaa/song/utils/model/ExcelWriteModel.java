package com.buaa.song.utils.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @FileName: ExcelWriteModel
 * @author: ProgrammerZhao
 * @Date: 2020/11/10
 * @Description:
 */

@Data
@ToString
public class ExcelWriteModel {

    @ExcelProperty("学号")
    private String studentId;

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("该学号之前是否已绑定账号")
    private String isAuth;

    @ExcelProperty("用户名")
    private String username;

    @ExcelProperty("密码")
    private String password;

    @ExcelProperty("备注")
    private String remark;

}