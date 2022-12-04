package com.buaa.song.dto;

import com.buaa.song.entity.Exam;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @FileName: CreateExamDto
 * @author: ProgrammerZhao
 * @Date: 2021/5/18
 * @Description:
 */
@Data
@ToString
@NoArgsConstructor
public class ExamDto {

    private Integer id;
    private String title;
    private String description;
    private Date startTime;
    private Date endTime;
    private String access;
    private Integer classId;
    private List<ExamProblemDto> problems;

    public ExamDto(Map<String,Object> map){
        this.id = (Integer) map.get("id");
        this.title = (String) map.get("title");
        this.description = (String) map.get("description");
        this.startTime = (Date) map.get("start_time");
        this.endTime = (Date) map.get("end_time");
        this.access = (String) map.get("access");
    }

    public Exam transformToExam(){
        Exam exam = new Exam();
        if(id != null){
            exam.setId(id);
        }
        exam.setTitle(title);
        exam.setDescription(description);
        exam.setStartTime(startTime);
        exam.setEndTime(endTime);
        exam.setAccess(access);
        exam.setClassId(classId);
        Date date = new Date(System.currentTimeMillis());
        exam.setCreateTime(date);
        exam.setUpdateTime(date);
        return exam;
    }


    @Data
    @NoArgsConstructor
    public static class ExamProblemDto{
        private Integer problemId;
        private String title;
        private Integer score;
        private Integer order;

        public ExamProblemDto(Map<String,Object> map){
            this.problemId = (Integer) map.get("id");
            this.title = (String) map.get("title");
            this.order = (Integer) map.get("order");
            this.score = (Integer) map.get("score");
        }


    }
}
