package com.buaa.song.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * @FileName: VerificationCode
 * @author: ProgrammerZhao
 * @Date: 2021/8/4
 * @Description:
 */
@Entity
@Data
@Table(name = "verification_code")
@NoArgsConstructor
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "email")
    private String email;

    @Column(name = "code")
    private String code;

    @Column(name = "expire_time")
    private Date expireTime;

    public VerificationCode(String email, String code, Date expireTime) {
        this.email = email;
        this.code = code;
        this.expireTime = expireTime;
    }
}