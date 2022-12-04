package com.buaa.song.dto;

import com.buaa.song.entity.Problem;
import com.buaa.song.utils.JsonUtil;
import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.*;

/**
 * @FileName: ProblemDto
 * @author: ProgrammerZhao
 * @Date: 2020/11/21
 * @Description:
 */
@Data
@ToString
public class ProblemDto {

    protected String title;
    protected String content;
    protected String access;
    protected Double difficulty;
    protected List<Integer> tags;
    protected Integer timeLimit;
    protected Integer memoryLimit;
    protected List<Integer> languages;
    protected List<TestDataDto> testdatas;
    protected Integer isSpecialJudge;
    protected UnSpecialJudge unSpecialJudge;
    protected SpecialJudge specialJudge;

    public Problem transferToProblem(){
        Problem problem = new Problem();
        problem.setTitle(title);
        problem.setContent(content);
        problem.setAccess(access);
        problem.setDifficulty(difficulty);
        Date date = new Date(System.currentTimeMillis());
        problem.setCreateTime(date);
        problem.setUpdateTime(date);
        problem.setTimeLimit(timeLimit);
        problem.setMemoryLimit(memoryLimit);
        problem.setIsSpecialJudge(isSpecialJudge);
        String jsonString;

        //不使用特殊评测规则
        Map<String, Object> problemSetting = generateProblemSetting();
        jsonString = JsonUtil.getJsonString(problemSetting);

        problem.setSetting(jsonString);

        return problem;
    }

    private Map<String, Object> generateProblemSetting() {
        Map<String, Object> setting = new HashMap<>();
        setting.put("time_limit", timeLimit);
        setting.put("mem_limit", memoryLimit);
        setting.put("process_limit", 1);

        if(!isSpecialJudge.equals(1)) { // 如果是普通判题
            setting.put("AC_level", unSpecialJudge.compareLevel);
            setting.put("PE_level", unSpecialJudge.peCompareLevel);
            setting.put("tab_width", unSpecialJudge.tabWidth);
            setting.put("case_insensitive", unSpecialJudge.isIgnoreCase);
        }

        // 非法系统调用号数组，例如[59, 60, 61]
        List<Integer> syscallNumberList = new ArrayList<Integer>() { {
            add(59);
        } };
        setting.put("rejected_syscall", syscallNumberList);

        List<Map<String, Object> > listOfTestDatas = generateTestPointsInSetting(isSpecialJudge);
        setting.put("test_points", listOfTestDatas);

        return setting;
    }

    private List<Map<String, Object> > generateTestPointsInSetting(Integer isSpecialJudge) {
        List<Map<String, Object> > listOfTestDatas = new ArrayList<>();
        for(TestDataDto testData : testdatas) {
            Map<String, Object> map = new HashMap<String, Object>() {{
                put("input", testData.getInput().getOriginalFilename());
                put("output", testData.getOutput().getOriginalFilename());
                put("weight", testData.getWeight());
            }};
            if(isSpecialJudge.equals(1)) {
                // 这里spj代码要改成文件, langugae要改成c、c++这样的英文
                map.put("spj", specialJudge.specialJudgeCode.getOriginalFilename());
                map.put("language", specialJudge.specialJudgeLanguageName);
            }
            listOfTestDatas.add(map);
        }

        return listOfTestDatas;
    }

    @Data
    @ToString
    public static class UnSpecialJudge {
        private Integer compareLevel;
        private Integer peCompareLevel;
        private Integer tabWidth;
        private Integer isIgnoreCase;
    }

    @Data
    @ToString
    public static class SpecialJudge {
        private String specialJudgeLanguageName;
        private MultipartFile specialJudgeCode;
    }
}