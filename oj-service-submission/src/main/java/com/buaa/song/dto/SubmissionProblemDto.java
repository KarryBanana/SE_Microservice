package com.buaa.song.dto;

import com.buaa.song.entity.Submission;
import com.buaa.song.exception.ParseToDateException;
import lombok.Data;
import lombok.ToString;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@ToString
public class SubmissionProblemDto {
    protected Integer languageId;
    protected String content;
    protected Integer codeLength;
    protected Integer problemId;
    protected String submitTime;
    protected String questionTime;

    public Submission transferToSubmission() throws ParseToDateException{

        Submission sub = new Submission();
        sub.setLanguage(languageId); // 注意这里是设置languageID
        sub.setCodeLength(codeLength);
        sub.setProblemId(problemId);

        try {
            Date createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(submitTime);
            sub.setCreateTime(createTime);
            sub.setUpdateTime(createTime);
        } catch (ParseException e) {
            throw new ParseToDateException(submitTime);
        }
        return sub;
    }
}
