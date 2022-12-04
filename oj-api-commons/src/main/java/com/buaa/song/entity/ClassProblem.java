package com.buaa.song.entity;


import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "class_problem")
@Data
public class ClassProblem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "class_id")
    private Integer class_id;

    @Column(name = "problem_id")
    private Integer problem_id;

    @Column(name = "create_user")
    private Integer create_user;

    @Column(name = "access", columnDefinition = "enum('public','protected','private')")
    private String access;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;
}
