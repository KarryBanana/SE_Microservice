package com.buaa.song.entity;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GeneratorType;

import javax.persistence.*;
import java.util.Date;

/**
 * @FileName: Course
 * @author: ProgrammerZhao
 * @Date: 2020/11/6
 * @Description:
 */

@Entity
@Table(name = "course")
@Data
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "`order`")
    private String order;

    @Column(name = "description")
    private String description;

    @Column(name = "create_time")
    private Date createTime;

}