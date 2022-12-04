package com.buaa.song.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

/**
 * @FileName: Exam
 * @author: ProgrammerZhao
 * @Date: 2021/4/6
 * @Description:
 */
@Entity
@Table(name = "exam")
@Data
@ToString
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "access",columnDefinition = "enum('public','protected','private')")
    private String access;

    @Column(name = "creator_id")
    private Integer creatorId;

    @Column(name = "class_id")
    private Integer classId;
}

