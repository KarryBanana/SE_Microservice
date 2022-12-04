package com.buaa.song.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.buaa.song.dao.SubmissionCodeDao;
import com.buaa.song.dao.SubmissionDao;
import com.buaa.song.dto.SubmissionProblemDto;
import com.buaa.song.entity.*;
import com.buaa.song.exception.LanguageNotFindException;
import com.buaa.song.exception.ParseToDateException;
import com.buaa.song.exception.SubmissionNotFindException;
import com.buaa.song.exception.UserNotFindException;
import com.buaa.song.result.Result;
import com.buaa.song.service.SubmissionService;
import com.buaa.song.utils.RestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @FileName: SubmissionServiceImpl
 * @author: ProgrammerZhao
 * @Date: 2021/5/21
 * @Description:
 */
@Service
public class SubmissionServiceImpl implements SubmissionService {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionServiceImpl.class);
    private static final String userServiceUrl = "http://oj-service-user";
    private static final String problemServiceUrl = "http://oj-service-problem";
    private static final String classServiceUrl = "http://oj-service-course";
    private static final String TASK_QUEUE_KEY = "task:queue";

    @Resource
    private RedisTemplate redisTemplate;

    @Autowired
    private SubmissionDao submissionDao;

    @Autowired
    private SubmissionCodeDao submissionCodeDao;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Result list() {

        return null;
    }

    @Override
    public Result findSubmissionById(Integer subId){
        Optional<Submission> sub = submissionDao.findById(subId);
        if (sub.isPresent()) {
            return Result.success("查询成功", sub.get());
        } else {
            return Result.fail(400, "查询失败");
        }
    }

    @Override
    public Result problemSubList(Integer userId, Integer cid, Integer pid){
        try {
            List<Map<String, Object>> problemSubList = null;

            Result result = checkUserExist(userId);
            User user = (User) result.getData();
            if(user.getRole().equals(3)) {
                // 查看题目所有的提交
                problemSubList = submissionDao.getProblemAdminSubList(pid);
                return Result.success(problemSubList);
            }
            result = checkUserIsClassMember(userId, cid);
            if(!result.getStatus().equals(200)){
                return Result.fail(400, "用户非班级成员!");
            }
            Integer classMemberType = (Integer) result.getData();

            if( classMemberType.equals(-1)) {
                return Result.fail(400, "用户非班级成员!");

            } else if(classMemberType.equals(0)) {  // 说明是班级的普通成员
                problemSubList = submissionDao.getProblemSubList(userId, pid);

            } else { // 说明是班级的管理员或创建者
                problemSubList = submissionDao.getProblemAdminSubList(pid);
            }
            return Result.success(problemSubList);

        } catch (UserNotFindException e) {
            logger.error(e.getMessage());
            return Result.fail(400, e.getMessage());
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result submitProblem(Integer userId, SubmissionProblemDto submissionProblemDto){
        try{
            checkUserExist(userId);

            SubmissionCode subCode = new SubmissionCode();
            Submission sub = submissionProblemDto.transferToSubmission();

            sub.setUserId(userId);
            sub.setResult("WT"); // 默认都是WT
            Submission savedSub = submissionDao.save(sub);

            String code = submissionProblemDto.getContent();
            System.out.println("code is " + code);
            subCode.setSubmissionId(savedSub.getId());
            subCode.setCode(code);
            submissionCodeDao.save(subCode);

            String questionTime = submissionProblemDto.getQuestionTime();

            Map<String, Object> task = SubToTask(savedSub, code, questionTime);
            redisTemplate.opsForList().leftPush(TASK_QUEUE_KEY, task);
        } catch (UserNotFindException  | LanguageNotFindException e) {  // SubToTask函数可能返回langId不存在
            logger.error(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(400, e.getMessage());
        } catch (ParseToDateException e) {
            logger.error(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(400, "日期格式错误");
        }
        return Result.success("代码提交成功!");
    }

    private Map<String, Object> SubToTask(Submission sub, String code, String questionTime) throws LanguageNotFindException{
        Map<String, Object> task = new HashMap<>();
        task.put("user_id", sub.getUserId());
        task.put("submission_id", sub.getId());
        System.out.println("subId is " + sub.getId());
        task.put("question_id", sub.getProblemId());
        task.put("content", code);

        // 需要将Date格式转化为String，不知道为什么JsonFormat没能成功
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        String submit_time = sdf.format(sub.getCreateTime());
        System.out.println("subCreateTime is " + submit_time);
        task.put("submit_time", submit_time.replace(' ', '_'));

        // 将题目的update_time转化成yyyy-MM-dd HH-mm-ss格式
        String update_time = questionTime.replace(':', '-').replace(' ', '_');
        task.put("question_date", update_time);

        Integer langId = sub.getLanguage();

        Result result = getLanguageById(langId);
        String langName = (String) result.getData();
        System.out.println("langId is "+langId + " langName is "+langName);

        task.put("language",langName);

        return task;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateSubResult(JSONObject data) {
        Integer subId = (Integer) data.get("submission_id");
        Integer problemId  = (Integer) data.get("problem_id");
        Integer judgeId = (Integer) data.get("judge_id");
        String judgeTime = (String) data.get("judge_time");
        String result = (String) data.get("result");
        Integer totalTime = (Integer) data.get("total_time_used");
        Integer totalMemory = (Integer) data.get("total_mem_used");
        Double score = (Double) data.get("score");  // 注意这里传参的时候要写成类似100.0
        try {
            Optional<Submission> optional = submissionDao.findById(subId);
            if(optional.isPresent()) {
                Submission sub = optional.get();
                sub.setResult(result);
                sub.setUpdateTime(new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").parse(judgeTime));
                sub.setTimeCost(totalTime);
                sub.setMemoryCost(totalMemory);
                sub.setScore(score);

                submissionDao.save(sub);
            } else {
                throw new SubmissionNotFindException(subId);
            }
        } catch (SubmissionNotFindException | ParseException e) {
            logger.error(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(400, e.getMessage());
        }
        return Result.success("更新提交"+subId+"成功");
    }

    private Result checkUserExist(Integer userId) throws UserNotFindException {
        Result result = RestUtil.get(restTemplate, userServiceUrl + "/user/" + userId, User.class);
        if (!result.getStatus().equals(200)) {
            throw new UserNotFindException(userId);
        }
        return result;
    }

    private Result checkUserIsClassMember(Integer userId, Integer classId) {
        Result result = RestUtil.get(restTemplate, classServiceUrl + "/class/" + classId + "/user/" + userId + "/isClassMember",
                Integer.class);
        return result;
    }

    private Result getLanguageById(Integer langId) throws LanguageNotFindException {
        Result result = RestUtil.get(restTemplate, problemServiceUrl + "/admin/lang/" + langId, String.class);
        if (!result.getStatus().equals(200)) {
            throw new LanguageNotFindException(langId);
        }
        return result;
    }
}