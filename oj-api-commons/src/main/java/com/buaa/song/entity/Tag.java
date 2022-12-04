package com.buaa.song.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @FileName: Tag
 * @author: ProgrammerZhao
 * @Date: 2020/11/16
 * @Description:
 */

@Data
@ToString
@Entity
@Table(name = "tag")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    // 前端需要获取tag下的题目数量，后端其实不太用得着
    @Column(name = "count")
    private Integer count;

}