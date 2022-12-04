package com.buaa.song.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

/**
 * @FileName: Membership
 * @author: ProgrammerZhao
 * @Date: 2020/11/8
 * @Description:
 */
@Entity
@Table(name = "membership")
@Data
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "class_id")
    private Integer classId;

    @Column(name = "type",columnDefinition = "enum('creator','admin','member')")
    private String type;

    @Column(name = "create_time")
    private Date createTime;

    public Membership(){}

    public Membership(Integer userId, Integer classId, String type, Date createTime) {
        this.userId = userId;
        this.classId = classId;
        this.type = type;
        this.createTime = createTime;
    }
}