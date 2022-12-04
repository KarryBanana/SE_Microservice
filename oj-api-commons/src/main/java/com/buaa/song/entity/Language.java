package com.buaa.song.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

/**
 * @FileName: Language
 * @author: ProgrammerZhao
 * @Date: 2020/11/21
 * @Description:
 */
@Entity
@Table(name = "language")
@Data
@ToString
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "info")
    private String info;

}