package com.buaa.song.dto;

import com.buaa.song.entity.Problem;
import com.buaa.song.utils.JsonUtil;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * @FileName: ProblemUpdateDto
 * @author: ProgrammerZhao
 * @Date: 2020/12/17
 * @Description:
 */
@Data
@ToString
public class ProblemUpdateDto extends ProblemDto {

    private List<Integer> deleteTestData;
    private List<UpdateTestData> updateTestData;

    public void updateProblem(Problem problem) {
        problem.setTitle(title);
        problem.setContent(content);
        // problem.setAccess(access);
        problem.setDifficulty(difficulty);
        problem.setTimeLimit(timeLimit);
        problem.setMemoryLimit(memoryLimit);
        problem.setUpdateTime(new Date(System.currentTimeMillis()));
        problem.setIsSpecialJudge(isSpecialJudge);
        String setting = null;
        if(isSpecialJudge.equals(0)){
            setting = JsonUtil.getJsonString(unSpecialJudge);
        } else {
            setting = JsonUtil.getJsonString(specialJudge);
        }
        problem.setSetting(setting);
    }

    @Data
    @ToString
    public static class UpdateTestData{
        private Integer id;
        private Double weight;
    }
}