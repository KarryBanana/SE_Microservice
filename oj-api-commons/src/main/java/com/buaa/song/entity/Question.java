package com.buaa.song.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

/**
 * @FileName: Question
 * @author: ProgrammerZhao
 * @Date: 2021/4/7
 * @Description:
 */
@Entity
@Table(name = "question")
@Data
@ToString
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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

    @Column(name = "reply")
    private String reply;

    @Column(name = "reply_person")
    private Integer replyPerson;

    @Column(name = "reply_time")
    private Date replyTime;
}