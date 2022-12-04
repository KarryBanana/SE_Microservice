package com.buaa.song.entity;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @FileName: SubmissionCode
 * @author: ProgrammerZhao
 * @Date: 2021/4/30
 * @Description:
 */
@Data
@ToString
@Entity
@Table(name = "submission_code")
public class SubmissionCode {

    @Id
    @Column(name = "submission_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "mygenerator")
    @GenericGenerator(name = "mygenerator", strategy = "com.buaa.song.generator.MyIdGenerator")
    private Integer submissionId;

    @Column(name = "code")
    private String code;
}