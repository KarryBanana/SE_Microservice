package com.buaa.song.entity;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @FileName: User
 * @author: ProgrammerZhao
 * @Date: 2020/10/27
 * @Description:
 */

@Entity
@Table(name = "user")
@Data
@Accessors(chain = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private Integer role;

    @Column(name = "privacy", columnDefinition = "enum('public','protected','private')")
    private String privacy;

    @Column(name = "description")
    private String description;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "school")
    private String school;

    @Column(name = "major")
    private String major;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "student_id")
    private String studentId;

    @Column(name = "realname")
    private String realName;

    @Column(name = "last_login")
    private Date lastLogin;

    public User(){}

    public User(String username, String password, Integer role, String nickname, Date createTime, Date updateTime) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.nickname = nickname;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

}