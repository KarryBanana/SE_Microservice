package com.buaa.song.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

/**
 * @FileName: Authentication
 * @author: ProgrammerZhao
 * @Date: 2020/11/9
 * @Description:
 */

@Data
@ToString
@Entity
@Table(name = "authentication")
public class Authentication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "student_id")
    private String studentId;

    @Column(name = "real_name")
    private String realName;

    public Authentication(){}

    public Authentication(Integer userId, String studentId, String realName) {
        this.userId = userId;
        this.studentId = studentId;
        this.realName = realName;
    }
}