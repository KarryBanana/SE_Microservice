package com.buaa.song.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

/**
 * @FileName: TestData
 * @author: ProgrammerZhao
 * @Date: 2020/12/16
 * @Description:
 */

@Entity
@Table(name = "test_data")
@Data
@ToString
public class TestData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "input")
    private String input;

    @Column(name = "output")
    private String output;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "problem_id")
    private Integer problemId;

    public TestData() {
    }

    public TestData(String input, String output, Double weight, Integer problemId) {
        this.input = input;
        this.output = output;
        this.weight = weight;
        this.problemId = problemId;
    }
}