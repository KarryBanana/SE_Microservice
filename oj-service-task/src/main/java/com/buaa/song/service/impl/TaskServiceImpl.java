package com.buaa.song.service.impl;

import com.buaa.song.constant.Constant;
import com.buaa.song.dao.IssueDao;
import com.buaa.song.dao.QuestionDao;
import com.buaa.song.dao.TaskDao;
import com.buaa.song.dto.CreateTaskDto;
import com.buaa.song.dto.CreateTaskDto.ExamProblemDto;
import com.buaa.song.dto.IssueDto;
import com.buaa.song.dto.RankDto;
import com.buaa.song.dto.RankDto.ProblemPenalty;
import com.buaa.song.dto.RankDto.UserRecord;
import com.buaa.song.entity.Issue;
import com.buaa.song.entity.Question;
import com.buaa.song.entity.Task;
import com.buaa.song.entity.User;
import com.buaa.song.exception.SaveFailException;
import com.buaa.song.exception.UserNotFindException;
import com.buaa.song.result.Result;
import com.buaa.song.service.TaskService;
import com.buaa.song.utils.RestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @FileName: TaskServiceImpl
 * @author: ProgrammerZhao
 * @Date: 2021/5/14
 * @Description:
 */
@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    private static final int errorPenalty = 20 * 60;

    @Autowired
    private TaskDao taskDao;
    @Autowired
    private IssueDao issueDao;
    @Autowired
    private QuestionDao questionDao;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Result taskList(Integer userId, Integer page, Integer limit) {
        try {
            User user = checkUserExist(userId);
            Integer roleId = user.getRole();
            List<Map<String, Object>> myTask = null;
            if (roleId.equals(2)) {
                myTask = taskDao.findMyTask(user.getId(),(page-1)*limit,limit);
            } else if (roleId.equals(3)){
                myTask = taskDao.findAllTask((page-1)*limit,limit);
            }
            return Result.success(myTask);
        } catch (UserNotFindException e) {
            log.error(e.getMessage());
            return Result.fail(400, e.getMessage());
        }
    }

    @Override
    public Result problemList(Integer userId, Integer courseId) {
        try {
            User user = checkUserExist(userId);
            Integer roleId = user.getRole();
            if(roleId.equals(2)){
                HashMap<String, Object> params = new HashMap<>();
                params.put("courseId",courseId);
                Result result = RestUtil.get(restTemplate, Constant.problemServiceUrl + "/problem/list/task", params, Map.class);
                return result;
            }else if(roleId.equals(3)){
                Result result = RestUtil.get(restTemplate, Constant.problemServiceUrl + "/problem/list/task", Map.class);
                return result;
            }
        } catch (UserNotFindException e) {
            log.error(e.getMessage());
            return Result.fail(400,e.getMessage());
        }
        return null;
    }

    @Override
    public Result info(Integer taskId) {
        List<Map<String, Object>> taskInfo = taskDao.getTaskInfo(taskId);
        return Result.success(taskInfo);
    }

    @Override
    public Result problem(Integer taskId, Integer order) {
        Map<String, Object> taskProblem = taskDao.getTaskProblem(taskId, order);
        return Result.success(taskProblem);
    }

    @Override
    public Result rank(Integer taskId) {
        RankDto rankDto = new RankDto();
        rankDto.setUpdateTime(new Date(System.currentTimeMillis()));

        List<Map<String, Object>> problemRecord = taskDao.getProblemRecord(taskId);
        rankDto.setProblemRecordsByMap(problemRecord);

        List<Map<String, Object>> userInfo =  taskDao.getTaskUserInfo(taskId);
        List<Map<String, Object>> subInfo =  taskDao.getTaskSubInfo(taskId);
        List<Map<String, Object>> earliestAcs =  taskDao.getEarliestAcOfProblem(taskId);

        List<UserRecord> userRecords = new LinkedList<>();
        int index = 0;
        for (Map<String, Object> u : userInfo) {
            UserRecord userRecord = new UserRecord();
            //?????????????????????????????????
            Integer userId = (Integer) u.get("id");
            userRecord.setUserId(userId);
            userRecord.setUsername((String) u.get("username"));
            userRecord.setStudentId((String) u.get("student_id"));
            userRecord.setName((String) u.get("name"));
            List<ProblemPenalty> penalties = new LinkedList<>();
            Double totalScore = 0.0;
            Long totalPenalty = 0L;
            for (Map<String, Object> p : problemRecord) {
                //????????????????????????????????????????????????
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
                //????????????????????????????????????????????????????????????
                if (userId.equals(userIdInSub) && problemId.equals(problemIdInSub)) {
                    String result = (String) sub.get("result");
                    Double score = (Double) sub.get("score");
                    Long time = Long.valueOf(sub.get("time").toString());
                    if (result.equalsIgnoreCase("ac")) {
                        //?????????????????????????????????????????????
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
                        //????????????????????????????????????????????????????????????????????????
                        int wrongTime = 0;
                        while (true) {
                            if (index >= subInfo.size()) {
                                break;
                            }
                            Map<String, Object> s = subInfo.get(index);
                            Integer userIdInSub1 = (Integer) s.get("user_id");
                            Integer problemIdInSub1 = (Integer) s.get("problem_id");
                            //?????????????????????????????????????????????????????????
                            if (userIdInSub1.equals(userId) && problemIdInSub1.equals(problemId)) {
                                String result1 = (String) s.get("result");
                                Long time1 = Long.valueOf(s.get("time").toString());
                                //???????????????????????????????????????ac?????????????????????????????????
                                if ((!result1.equalsIgnoreCase("ac")) && (time1.compareTo(time) < 0)) {
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
                        //??????????????????????????????????????????????????????
                        penalty.setStatus(3);
                        penalty.setPenalty(time);
                        //??????????????????
                        int wrongTime = 0;
                        while (true) {
                            if (index >= subInfo.size()) {
                                break;
                            }
                            Map<String, Object> s = subInfo.get(index);
                            Integer userIdInSub1 = (Integer) s.get("user_id");
                            Integer problemIdInSub1 = (Integer) s.get("problem_id");
                            //?????????????????????????????????????????????????????????
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
                        //?????????????????????????????????
                        penalty.setStatus(2);
                        //??????????????????
                        int wrongTime = 0;
                        while (true) {
                            if (index >= subInfo.size()) {
                                break;
                            }
                            Map<String, Object> s = subInfo.get(index);
                            Integer userIdInSub1 = (Integer) s.get("user_id");
                            Integer problemIdInSub1 = (Integer) s.get("problem_id");
                            //?????????????????????????????????????????????????????????
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
    public Result getIssue(Integer taskId) {
        List<Map<String, Object>> issue = taskDao.getTaskIssue(taskId);
        return Result.success(issue);
    }

    @Override
    public Result createIssue(IssueDto issueDto, Integer taskId, Integer userId) {
        Issue issue = new Issue();
        issue.setTitle(issueDto.getTitle());
        issue.setContent(issueDto.getContent());
        issue.setCreatorId(userId);
        issue.setTaskId(taskId);
        issue.setCreateTime(new Date(System.currentTimeMillis()));
        Integer problemId = taskDao.findProblemIdByOrder(taskId, issueDto.getOrder());
        issue.setProblemId(problemId);
        issueDao.save(issue);
        return Result.success();
    }

    @Override
    public Result question1(Integer taskId) {
        List<Map<String, Object>> question = questionDao.getReplyed(taskId);
        return Result.success(question);
    }

    @Override
    public Result question2(Integer taskId) {
        List<Map<String, Object>> question = questionDao.getUnreply(taskId);
        return Result.success(question);
    }

    @Override
    public Result answer(Integer questionId, Integer userId, String reply) {
        Optional<Question> optionalQuestion = questionDao.findById(questionId);
        if(optionalQuestion.isPresent()){
            Question question = optionalQuestion.get();
            question.setReply(reply);
            question.setReplyPerson(userId);
            question.setReplyTime(new Date(System.currentTimeMillis()));
            questionDao.save(question);
            return Result.success();
        }else{
            return Result.fail(404,"??????????????????");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result createTask(CreateTaskDto taskDto, Integer userId) {
        try {
            Task task = taskDto.transform();
            checkUserExist(userId);
            task.setCreatorId(userId);
            Task savedTask = taskDao.save(task);
            Integer taskId = savedTask.getId();
            List<ExamProblemDto> problems = taskDto.getProblems();
            for(ExamProblemDto problemDto : problems){
                Integer problemId = problemDto.getProblemId();
                Integer order = problemDto.getOrder();
                Double score = problemDto.getScore();
                int rs = taskDao.saveTaskProblem(taskId, userId, problemId, order, score);
                if(!(rs > 0)){
                    throw new SaveFailException("???????????????????????????ID???"+problemId+",??????ID???"+taskId);
                }
            }
            return Result.success("??????????????????");
        } catch (UserNotFindException e) {
            log.error(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(404,"???????????????");
        } catch (SaveFailException e) {
            log.error(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(400,"??????????????????");
        }
    }

    private User checkUserExist(Integer userId) throws UserNotFindException {
        Result result = RestUtil.get(restTemplate, Constant.userServiceUrl + "/user/" + userId + "/", User.class);
        if (!result.getStatus().equals(200)) {
            throw new UserNotFindException(userId);
        }
        return (User)result.getData();
    }

}