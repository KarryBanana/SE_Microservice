package com.buaa.song.service.impl;

import com.buaa.song.constant.Constant;
import com.buaa.song.dao.*;
import com.buaa.song.dto.ExamDto;
import com.buaa.song.dto.ExamDto.ExamProblemDto;
import com.buaa.song.dto.PageAndSortDto;
import com.buaa.song.dto.RankDto;
import com.buaa.song.dto.RankDto.ProblemPenalty;
import com.buaa.song.dto.RankDto.UserRecord;
import com.buaa.song.entity.Class;
import com.buaa.song.entity.*;
import com.buaa.song.exception.ExamNotFindException;
import com.buaa.song.exception.OjClassNotFindException;
import com.buaa.song.exception.SaveFailException;
import com.buaa.song.exception.UserNotFindException;
import com.buaa.song.result.Result;
import com.buaa.song.service.ExamService;
import com.buaa.song.utils.RestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @FileName: ExamServiceImpl
 * @author: ProgrammerZhao
 * @Date: 2021/4/7
 * @Description:
 */
@Service
@Slf4j
public class ExamServiceImpl implements ExamService {

    private final int errorPenalty = 20 * 60;
    @Autowired
    private ExamDao examDao;
    @Autowired
    private IssueDao issueDao;
    @Autowired
    private QuestionDao questionDao;
    @Autowired
    private SubmissionDao submissionDao;
    @Autowired
    private SubmissionCodeDao submissionCodeDao;
    @Autowired
    private RestTemplate restTemplate;

    private static final String classClientUrl = "http://oj-service-course";

    @Override
    public Result getInfo(Integer examId) {
        Map<String, Object> examInfo = examDao.getExamInfo(examId);
        if (examInfo == null || examInfo.size() == 0) {
            return Result.fail(404, "考试不存在");
        } else {
            return Result.success(examInfo);
        }
    }

    @Override
    public Result getProblems(Integer examId) {
        List<Map<String, Object>> problems = examDao.getProblemList(examId);
        return Result.success(problems);
    }

    @Override
    public Result getIssues(Integer examId) {
        List<Map<String, Object>> issues = issueDao.getIssuesByExamId(examId);
        return Result.success(issues);
    }

    @Override
    public Result getQuestion(Integer examId) {
        List<Map<String, Object>> questions = questionDao.getQuestionsByExamId(examId);
        return Result.success(questions);
    }

    @Override
    public Result getRank(Integer examId) {
        RankDto rankDto = new RankDto();
        rankDto.setUpdateTime(new Date(System.currentTimeMillis()));

        List<Map<String, Object>> problemRecord = examDao.getProblemRecord(examId);  // 获取比赛题目提交、通过人数
        rankDto.setProblemRecordsByMap(problemRecord);

        List<Map<String, Object>> userInfo = examDao.getExamUserInfo(examId);  // 获取参加比赛的所有学生的id、学号、用户名
        List<Map<String, Object>> subInfo = examDao.getExamSubInfo(examId);  // 获取比赛的所有提交信息，按照用户ID、题目顺序、提交分数、提交时间排序
        List<Map<String, Object>> earliestAcs = examDao.getEarliestAcOfProblem(examId);  // 获取比赛题目的最早AC的userID

        List<UserRecord> userRecords = new LinkedList<>();
        int index = 0;
        for (Map<String, Object> u : userInfo) {
            UserRecord userRecord = new UserRecord();
            //获取并设置用户基本信息
            Integer userId = (Integer) u.get("id");
            userRecord.setUserId(userId);
            userRecord.setUsername((String) u.get("username"));
            userRecord.setStudentId((String) u.get("student_id"));
            userRecord.setName((String) u.get("name"));
            List<ProblemPenalty> penalties = new LinkedList<>();
            Double totalScore = 0.0;
            Long totalPenalty = 0L;
            for (Map<String, Object> p : problemRecord) {
                //获取每个用户对每道题目的提交情况
                Integer problemId = (Integer) p.get("problem_id");
                Integer order = (Integer) p.get("order");
                ProblemPenalty penalty = new ProblemPenalty();
                penalty.setProblemId(problemId);
                Double problemScore = 0.0;
                Long problemPenalty = 0L;

                if (index >= subInfo.size()) {
                    penalty.setStatus(1);
                    penalties.add(penalty);
                    continue;
                }
                Map<String, Object> sub = subInfo.get(index);
                Integer userIdInSub = (Integer) sub.get("user_id");
                Integer problemIdInSub = (Integer) sub.get("problem_id");
                //当前提交记录是该用户关于该题目的提交记录
                if (userId.equals(userIdInSub) && problemId.equals(problemIdInSub)) {
                    String result = (String) sub.get("result");
                    Double score = (Double) sub.get("score");
                    Long time = Long.valueOf(sub.get("time").toString());
                    if (result.equalsIgnoreCase("ac")) {
                        //记录提交状态为已通过，记录罚时
                        penalty.setStatus(4);
                        for (Map<String, Object> earliestAc : earliestAcs) {
                            Integer earliestAcProblemId = (Integer) earliestAc.get("problem_id");
                            Integer earliestAcUserId = (Integer) earliestAc.get("user_id");
                            if (userId.equals(earliestAcUserId) && problemId.equals(earliestAcProblemId)) {
                                penalty.setStatus(5);
                                break;
                            }
                        }
                        penalty.setPenalty(time);
                        //遍历从这条提交记录后所有的提交记录，计算错误次数
                        int wrongTime = 0;
                        while (true) {
                            if (index >= subInfo.size()) {
                                break;
                            }
                            Map<String, Object> s = subInfo.get(index);
                            Integer userIdInSub1 = (Integer) s.get("user_id");
                            Integer problemIdInSub1 = (Integer) s.get("problem_id");
                            //该提交记录是该用户关于该题目的提交记录
                            if (userIdInSub1.equals(userId) && problemIdInSub1.equals(problemId)) {
                                String result1 = (String) s.get("result");
                                Long time1 = Long.valueOf(s.get("time").toString());
                                // 若该次提交未通过且在第一次ac前提交，则计入错误次数
                                // 但CE不计入错误次数
                                if ((!result1.equalsIgnoreCase("ac")) && ( !result1.equalsIgnoreCase("ce") )
                                                                         && (time1.compareTo(time) < 0)) {
                                    wrongTime++;
                                }
                                index++;
                            } else {
                                break;
                            }
                        }
                        penalty.setWrongTime(wrongTime);
                        problemPenalty += (time + errorPenalty * wrongTime);
                    } else if (score.compareTo(0.0) > 0) {
                        //记录提交状态为得分但未通过，记录罚时
                        penalty.setStatus(3);
                        penalty.setPenalty(time);
                        //计算错误次数
                        int wrongTime = 0;
                        while (true) {
                            if (index >= subInfo.size()) {
                                break;
                            }
                            Map<String, Object> s = subInfo.get(index);
                            Integer userIdInSub1 = (Integer) s.get("user_id");
                            Integer problemIdInSub1 = (Integer) s.get("problem_id");
                            //该提交记录是该用户关于该题目的提交记录
                            if (userIdInSub1.equals(userId) && problemIdInSub1.equals(problemId)) {
                                wrongTime++;
                                index++;
                            } else {
                                break;
                            }
                        }
                        penalty.setWrongTime(wrongTime);
                        problemPenalty += (time + errorPenalty * wrongTime);
                    } else {
                        //记录状态为做题但未得分
                        penalty.setStatus(2);
                        //计算错误次数
                        int wrongTime = 0;
                        while (true) {
                            if (index >= subInfo.size()) {
                                break;
                            }
                            Map<String, Object> s = subInfo.get(index);
                            Integer userIdInSub1 = (Integer) s.get("user_id");
                            Integer problemIdInSub1 = (Integer) s.get("problem_id");
                            //该提交记录是该用户关于该题目的提交记录
                            if (userIdInSub1.equals(userId) && problemIdInSub1.equals(problemId)) {
                                wrongTime++;
                                index++;
                            } else {
                                break;
                            }
                        }
                        penalty.setWrongTime(wrongTime);
                    }
                    problemScore = score;
                } else {
                    penalty.setStatus(1);
                    problemScore = 0.0;
                }
                penalties.add(penalty);
                totalScore += problemScore;
                totalPenalty += problemPenalty;
            }
            userRecord.setScore(totalScore);
            userRecord.setPenalty(totalPenalty);
            userRecord.setProblemPenalties(penalties);

            userRecords.add(userRecord);
        }

        Collections.sort(userRecords);
        int order = 1;
        for (UserRecord record : userRecords) {
            record.setOrder(order);
            order++;
        }

        rankDto.setUserRecords(userRecords);
        return Result.success(rankDto);
    }

    @Override
    public Result getSubmission(Integer examId, Integer userId) {
        List<Map<String, Object>> sub = examDao.getSubmission(examId, userId);
        return Result.success(sub);
    }

    @Override
    public Result askQuestion(String content, Integer examId, Integer problemId, Integer userId) {
        Question question = new Question();
        question.setContent(content);
        question.setCreateTime(new Date(System.currentTimeMillis()));
        question.setCreatorId(userId);
        question.setProblemId(problemId);
        question.setExamId(examId);
        Question save = questionDao.save(question);
        if (save != null) {
            return Result.success();
        } else {
            return Result.fail(400, "提问失败");
        }
    }

    @Override
    public Result submit(Integer userId, Integer examId, Integer problem, Integer language, String code) {
        Submission submission = new Submission();
        submission.setLanguage(language);
        submission.setUserId(userId);
        submission.setProblemId(problem);
        submission.setExamId(examId);
        submission.setResult("WT");
        Date date = new Date(System.currentTimeMillis());
        submission.setCreateTime(date);
        submission.setUpdateTime(date);
        submission.setCodeLength(code.length());
        Submission save = submissionDao.save(submission);

        SubmissionCode submissionCode = new SubmissionCode();
        submissionCode.setSubmissionId(save.getId());
        submissionCode.setCode(code);
        submissionCodeDao.save(submissionCode);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result editExam(ExamDto examDto, Integer userId) {
        try {
            checkUserExist(userId);
            Exam exam = examDto.transformToExam();
            Integer examId = exam.getId();
            exam.setCreatorId(userId);

            examDao.save(exam);
            List<ExamProblemDto> newProblems = examDto.getProblems();
            List<Map<String, Object>> oldProblems = examDao.getExamProblems(examId);

            // ckr疑惑：这么写会不会比较麻烦
            for(Map<String,Object> oldProblemMap : oldProblems){
                ExamProblemDto oldProblem = new ExamProblemDto(oldProblemMap);
                boolean isChanged = true;
                for(ExamProblemDto newProblem : newProblems){
                    if(oldProblem.equals(newProblem)){
                        isChanged = false;
                        newProblems.remove(newProblem);
                        break;
                    }
                }
                if(isChanged){
                    examDao.deleteExamProblem(examId,oldProblem.getProblemId());
                }
            }
            for(ExamProblemDto p : newProblems){
                examDao.saveExamProblem(examId,p.getProblemId(),p.getOrder(),userId,p.getScore());
            }
            return Result.success();
        } catch (UserNotFindException e) {
            log.error(e.getMessage());
            return Result.fail(400,"用户不存在");
        }
    }

    @Override
    public void test() {
        Map<String, Object> test = examDao.test(null);
        System.out.println(test);
    }

    @Override
    public Result getAllSubmission() {
        return null;
    }

    @Override
    public Result getExamEdit(Integer examId) {
        Map<String, Object> examInfo = examDao.getExamInfo(examId);
        ExamDto examDto = new ExamDto(examInfo);
        List<Map<String, Object>> examProblems = examDao.getExamProblems(examId);
        List<ExamProblemDto> problems = new LinkedList<>();
        for(Map<String,Object> problem : examProblems){
            ExamProblemDto problemDto = new ExamProblemDto(problem);
            problems.add(problemDto);
        }
        examDto.setProblems(problems);
        return Result.success(examDto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteExam(Integer examId) {
        Optional<Exam> optionalExam = examDao.findById(examId);
        try {
            if(optionalExam.isPresent()) {
                examDao.deleteById(examId);
                return Result.success("删除比赛成功!");
            } else {
                throw new ExamNotFindException(examId);
            }
        } catch (ExamNotFindException e) {
            log.error(e.getMessage());
            return Result.fail(400, e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result createExam(ExamDto examDto, Integer userId) {
        try {
            Exam exam = examDto.transformToExam();
            exam.setCreatorId(userId);
            Exam savedExam = examDao.save(exam);
            Integer examId = savedExam.getId();
            List<ExamProblemDto> problems = examDto.getProblems();
            for(ExamProblemDto p : problems){
                Integer order = p.getOrder();
                Integer problemId = p.getProblemId();
                Integer score = p.getScore();
                int rs = examDao.saveExamProblem(examId, problemId, order, userId, score);
                if(rs <= 0){
                    throw new SaveFailException("保存考试题目失败,考试ID为"+examId+",题目ID为"+problemId);
                }
            }
            return Result.success();
        } catch (SaveFailException e) {
            log.error(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(400,"保存考试题目失败");
        }
    }

    @Override
    public Result list(Integer userId, PageAndSortDto page) {
        try {
            Integer start = page.getPage();
            Integer limit = page.getLimit();

            User user = checkUserExist(userId);
            Integer roleId = user.getRole();
            if(roleId.equals(2)){
                // sql语句后面可以修改的简洁点
                List<Map<String, Object>> myExams = examDao.getMyExams(userId,(start-1)*limit,limit);
                return Result.success(myExams);
            }else if(roleId.equals(3)){
                // sql语句后面可以修改的简洁点
                List<Map<String, Object>> allExams = examDao.getAllExams((start-1)*limit,limit);
                return Result.success(allExams);
            }else {
                return Result.fail(400,"用户权限错误");
            }
        } catch (UserNotFindException e) {
            log.error(e.getMessage());
            return Result.fail(400,e.getMessage());
        }
    }

    @Override
    public Result getListPageNum(Integer userId, Integer limit) {
        try {
            User user = checkUserExist(userId);
            Integer roleId = user.getRole();
            Integer count;
            if(roleId.equals(2)) {
                count = examDao.getMyExamsPageNum(userId);
            } else if(roleId.equals(3)) { // 超级管理员能看到所有比赛
                count = examDao.getAllExamsPageNum();
            } else {
                return Result.fail(400,"用户权限错误");
            }

            Integer pageNumber;
            if(count % limit == 0) {
                pageNumber = count / limit;
            } else {
                pageNumber = count / limit + 1;
            }

            return Result.success(pageNumber);
        } catch (UserNotFindException e) {
            log.error(e.getMessage());
            return Result.fail(400,e.getMessage());
        }
    }

    @Override
    public Result getClassExam(Integer userId, Integer classId, PageAndSortDto page) {
        try {
            checkUserExist(userId);
            checkClassExist(classId);

            Integer start = page.getPage();
            Integer limit = page.getLimit();

            List<Map<String, Object>> classExams = examDao.getClassExam(userId, classId, (start - 1) * limit, limit);
            return Result.success(classExams);

        } catch (UserNotFindException | OjClassNotFindException e) {
            log.error(e.getMessage());
            return Result.fail(400, e.getMessage());
        }
    }

    @Override
    public Result getCode(Integer id) {
        Optional<SubmissionCode> optionalCode = submissionCodeDao.findById(id);
        if(optionalCode.isPresent()){
            return Result.success(optionalCode.get());
        }else {
            return Result.fail(404,"代码不存在");
        }
    }

    // 设置班级比赛权限
    @Override
    public Result setExamAccess(Integer classId, Integer examId, Integer access) {
        Optional<Exam> exam = examDao.findById(examId);
        if(exam.isPresent()) {
            Integer examOfClassId = exam.get().getClassId();
            if(!classId.equals(examOfClassId)) {
                return Result.fail(400, "班级比赛关系不存在");
            }
            if(access.equals(0)) {
                exam.get().setAccess("public");
            } else if(access.equals(1)) {
                exam.get().setAccess("protected");
            } else if(access.equals(2)) {
                exam.get().setAccess("private");
            } else {
                return Result.success("比赛权限错误");
            }
            examDao.save(exam.get());
            return Result.success("比赛权限设置成功");
        } else {
            return Result.fail(400, "比赛不存在");
        }
    }

    private User checkUserExist(Integer userId) throws UserNotFindException {
        Result result = RestUtil.get(restTemplate, Constant.userServiceUrl + "/user/" + userId, User.class);
        if (!result.getStatus().equals(200)) {
            throw new UserNotFindException(userId);
        }
        return (User)result.getData();
    }


    /*
        /user_class/id 收不到request token
     */
    private Result checkClassExist(Integer classId) throws OjClassNotFindException {
        Result result = RestUtil.get(restTemplate, classClientUrl + "/class/" + classId, Class.class);
        if (!result.getStatus().equals(200)) {
            throw new OjClassNotFindException(classId);
        }
        return result;
    }

}
