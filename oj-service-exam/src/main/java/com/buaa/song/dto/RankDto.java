package com.buaa.song.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigInteger;
import java.util.*;

/**
 * @FileName: RankDto
 * @author: ProgrammerZhao
 * @Date: 2021/4/9
 * @Description:
 */
@Data
@NoArgsConstructor
public class RankDto {

    private Date updateTime;
    private List<ProblemRecord> problemRecords;
    private List<UserRecord> userRecords;

    public void setProblemRecordsByMap(List<Map<String,Object>> records){
        List <ProblemRecord> recordList = new LinkedList<>();
        for(Map<String,Object> record : records){
            recordList.add(new ProblemRecord(record));
        }
        setProblemRecords(recordList);
    }

    @Data
    @NoArgsConstructor
    public static class ProblemRecord implements Comparable<ProblemRecord>{

        private Integer problemId;
        private Integer problemOrder;
        private Integer submissionUserNum;
        private Integer acceptedUserNum;

        @Override
        public int compareTo(ProblemRecord o) {
            return this.problemOrder.compareTo(o.problemOrder);
        }

        public ProblemRecord(Map<String,Object> record){
            this.problemId = (Integer) record.get("problem_id");
            this.problemOrder = (Integer) record.get("order");
            this.submissionUserNum = Integer.valueOf((record.get("sub_user_num")).toString());
            this.acceptedUserNum = Integer.valueOf((record.get("ac_user_num")).toString());
        }
    }

    @Data
    public static class UserRecord implements Comparable<UserRecord>{
        private Integer userId;
        private String name;
        private String username;
        private String studentId;
        private Double score;
        private Long penalty;
        private Integer order;
        private List<ProblemPenalty> problemPenalties;

        @Override
        public int compareTo(UserRecord o) {
            if(!this.score.equals(o.score)){
                return o.score.compareTo(this.score);
            }else{
                return this.penalty.compareTo(o.penalty);
            }
        }
    }

    @Data
    public static class ProblemPenalty {
        private Integer problemId;
        private Integer status; //1代表未做题，2代表做题但未得分，3代表得分但未通过，4代表通过，5代表第一个通过
        private Long penalty;
        private Integer wrongTime;
    }
}