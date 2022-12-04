package com.buaa.song.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @FileName: Class
 * @author: ProgrammerZhao
 * @Date: 2020/11/7
 * @Description:
 */

@Entity
@Table(name = "class")
@Data
public class Class {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "course_id")
    private Integer courseId;

    @Column(name = "access",columnDefinition = "enum('public','protected','private')")
    private String access;

    @Column(name = "`order`")
    private Integer order;

    @Column(name = "creator")
    private Integer creator;

    @Column(name = "create_time")
    private Date createTime;

}