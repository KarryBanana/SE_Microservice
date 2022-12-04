package com.buaa.song.dto;

import cn.hutool.db.DaoTemplate;
import com.buaa.song.entity.Task;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * @FileName: CreateTaskDto
 * @author: ProgrammerZhao
 * @Date: 2021/5/19
 * @Description:
 */
@Data
@ToString
public class CreateTaskDto {
    private String title;
    private String description;
    private Date startTime;
    private Date endTime;
    private String access;
    private Integer courseId;
    private List<ExamProblemDto> problems;

    public Task transform(){
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setStartTime(startTime);
        task.setEndTime(endTime);
        task.setAccess(access);
        task.setCourseId(courseId);
        Date date = new Date(System.currentTimeMillis());
        task.setCreateTime(date);
        task.setUpdateTime(date);

        return task;
    }

    @Data
    @ToString
    public static class ExamProblemDto{
        private Integer problemId;
        private Double score;
        private Integer order;
    }
}