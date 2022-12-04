package com.buaa.song.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

/**
 * @FileName: Issue
 * @author: ProgrammerZhao
 * @Date: 2021/4/7
 * @Description:
 */
@Entity
@Table(name = "issue")
@Data
@ToString
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "creator_id")
    private Integer creatorId;

    @Column(name = "exam_id")
    private Integer examId;

    @Column(name = "task_id")
    private Integer taskId;

    @Column(name = "problem_id")
    private Integer problemId;

}