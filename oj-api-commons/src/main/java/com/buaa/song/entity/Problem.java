package com.buaa.song.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @FileName: Problem
 * @author: ProgrammerZhao
 * @Date: 2020/11/6
 * @Description:
 */

@Entity
@Table(name = "problem")
@Data
@ToString
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "difficulty")
    private Double difficulty;

    @Column(name = "access", columnDefinition = "enum('public', 'private')")
    private String access;

    @Column(name = "create_time")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "creator_id")
    private Integer creatorId;

    @Column(name = "time_limit")
    private Integer timeLimit;

    @Column(name = "memory_limit")
    private Integer memoryLimit;

    @Column(name = "is_special_judge")
    private Integer isSpecialJudge;

    @Column(name = "setting")
    private String setting;

}