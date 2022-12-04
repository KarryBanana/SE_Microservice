package com.buaa.song.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

/**
 * @FileName: ApplyCourse
 * @author: ProgrammerZhao
 * @Date: 2020/11/14
 * @Description:
 */
@Data
@ToString
@Entity
@Table(name = "apply_class")
public class ApplyClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "class_id")
    private Integer classId;

    @Column(name = "info")
    private String info;

    @Column(name = "apply_time")
    private Date applyTime;

    @Column(name = "is_agree")
    private Integer isAgree;

    @Column(name = "deal_person")
    private Integer dealPerson;

    @Column(name = "deal_time")
    private Date dealTime;

    public ApplyClass() {}

    public ApplyClass(Integer userId, Integer classId, String info) {
        this.userId = userId;
        this.classId = classId;
        this.info = info;
    }
}