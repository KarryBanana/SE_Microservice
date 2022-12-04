package com.buaa.song.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

/**
 * @FileName: Register
 * @author: ProgrammerZhao
 * @Date: 2020/10/29
 * @Description:
 */

@Entity
@Table(name = "register")
@Data
@ToString
public class Register {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "apply_role")
    private Integer applyRole;

    @Column(name = "info")
    private String info;

    @Column(name = "register_time")
    private Date registerTime;

    @Column(name = "status")
    private Integer status;

    @Column(name = "check_person")
    private Integer checkPerson;

    @Column(name = "check_time")
    private Date checkTime;

}