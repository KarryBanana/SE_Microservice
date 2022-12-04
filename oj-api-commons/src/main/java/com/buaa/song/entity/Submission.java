package com.buaa.song.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @FileName: Submission
 * @author: ProgrammerZhao
 * @Date: 2021/4/9
 * @Description:
 */
@Entity
@Table(name = "submission")
@Data
@ToString
public class Submission{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 注意对应的是language_id
    @Column(name = "language_id")
    private Integer language;

    @Column(name = "result",columnDefinition = "enum('WT','JG','AC','WA','CE','REG','MLE','REP','PE','TLE')")
    private String result;

    @Column(name = "score")
    private Double score;

    @Column(name = "code_length")
    private Integer codeLength;

    @Column(name = "time_cost")
    private Integer timeCost;

    @Column(name = "memory_cost")
    private Integer memoryCost;

    @Column(name = "detail")
    private String detail;

    @Column(name = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss")
    private Date createTime;

    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "problem_id")
    private Integer problemId;

    @Column(name = "judge_id")
    private Integer judgeId;

    @Column(name = "exam_id")
    private Integer examId;


}