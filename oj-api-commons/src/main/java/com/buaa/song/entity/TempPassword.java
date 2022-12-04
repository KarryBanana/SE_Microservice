package com.buaa.song.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

/**
 * @FileName: TempPassword
 * @author: ProgrammerZhao
 * @Date: 2021/4/7
 * @Description:
 */
@Entity
@Table(name = "temp_password")
@Data
@ToString
public class TempPassword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "exam_id")
    private Integer examId;

    @Column(name = "temp_password")
    private String tempPassword;

    @Column(name = "is_available")
    private Integer isAvailable;
}