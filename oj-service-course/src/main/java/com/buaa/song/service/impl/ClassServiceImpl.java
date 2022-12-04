package com.buaa.song.service.impl;


import com.buaa.song.dao.ApplyClassDao;
import com.buaa.song.dao.ClassDao;
import com.buaa.song.dao.MembershipDao;
import com.buaa.song.dto.ApplyClassDto;
import com.buaa.song.dto.PageAndSortDto;
import com.buaa.song.entity.Class;
import com.buaa.song.entity.*;
import com.buaa.song.exception.ExcelFormatException;
import com.buaa.song.exception.OjClassNotFindException;
import com.buaa.song.exception.UserNotFindException;
import com.buaa.song.result.Result;
import com.buaa.song.service.ClassService;
import com.buaa.song.utils.RestUtil;
import com.buaa.song.utils.excelUtils.ExcelReadListener;
import com.buaa.song.utils.excelUtils.ExcelUtil;
import com.buaa.song.utils.model.ExcelReadModel;
import com.buaa.song.utils.model.ExcelWriteModel;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @FileName: ClassServiceImpl
 * @author: ProgrammerZhao
 * @Date: 2020/11/7
 * @Description:
 */
@Service
@RefreshScope
@Slf4j
public class ClassServiceImpl implements ClassService {
    // logger是记录日志吧
    private static final Logger logger = LoggerFactory.getLogger(ClassServiceImpl.class);

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private ClassDao classDao;

    @Autowired MembershipDao membershipDao;

    @Autowired ApplyClassDao applyClassDao;

    @Autowired
    private RestTemplate restTemplate;

    private static final String userServiceUrl = "http://oj-service-user";

    private static final Integer CLASS_MEMBER = 0;
    private static final Integer CLASS_ADMIN = 1;
    private static final Integer CLASS_CREATOR = 2;

    // 判断是否是班级成员
    public Result isClassMember(Integer userId, Integer classId) {
        Membership member = membershipDao.findByUserIdAndClassId(userId, classId);
        if (member == null) {
            return Result.success(-1); // 非班级成员
        } else if (member.getType().equals("admin")) {
            return Result.success(CLASS_ADMIN);
        } else if (member.getType().equals("creator")) {
            return Result.success(CLASS_CREATOR);
        } else {
            return Result.success(CLASS_MEMBER);
        }
    }

    // 学生端 获取班级信息
    @Override
    public Result getClassInfo(Integer classId) {
        Optional<Class> ojClass = classDao.findById(classId);
        if(ojClass.isPresent()) {

            String name = ojClass.get().getName();
            String info = ojClass.get().getDescription();
            Map<String, Object> classInfo = new HashMap<String, Object>();
            classInfo.put("name", name);
            classInfo.put("info", info);

            return Result.success(classInfo);
        }
        return Result.fail(400, "该班级不存在");
    }

    // 学生端 通过班级ID获取班级
    @Override
    public Result findClassById(Integer classId) {
        Optional<Class> ojClass = classDao.findById(classId);
        if (ojClass.isPresent()) {
            return Result.success("查询成功", ojClass.get());
        }
        return Result.fail(400, "该课程不存在");
    }

    // 学生端 通过用户ID获取班级列表
    public Result getClassList(Integer userId) {
        try {
            Result result = checkUserExist(userId);
            User user = (User) result.getData();

            List<Map<String, Object>> classList = null;

            if(user.getRole().equals(2)) {
                classList = classDao.getUserClassList(userId);
            } else if( user.getRole().equals(3)) { // 超级管理员查看所有的班级
                classList = classDao.findAllClass();
            } else {
                return Result.fail(400, "用户权限错误");
            }
            return Result.success(classList);
        } catch (UserNotFindException e) {
            logger.error(e.getMessage());
            return Result.fail(400, e.getMessage());
        }
    }

    // 学生端 通过班级ID获取对应的课程信息
    @Override
    public Result getClassCourse(Integer classId) {
        Optional<Class> ojClass = classDao.findById(classId);
        if(ojClass.isPresent()) {
            List<Map<String, Object>> course = classDao.getCourseByClassId(classId);
            return Result.success(course);
        }
        return Result.fail(400, "该班级不存在");
    }

    // 学生端 通过班级ID获取班级成员
    @Override
    public Result getClassMember(Integer classId) {
        Optional<Class> ojClass = classDao.findById(classId);
        if(ojClass.isPresent()) {

            // 获取班级的成员信息
            List<Map<String, Object>> classMember = classDao.getClassStudent(classId);

            return Result.success(classMember);
        }
        return Result.fail(400, "该课程不存在");
    }

    // 学生端 申请加入课程
    public Result applyClass(Integer userId, ApplyClassDto applyClassDto){
        try {
            checkUserExist(userId);
            Integer classId = applyClassDto.getClassId();
            Optional<Class> optional = classDao.findById(classId);
            if(optional.isPresent()) {
                String info = applyClassDto.getInfo();

                ApplyClass applyClass = new ApplyClass(userId, classId, info);

                Date date = new Date(System.currentTimeMillis());
                applyClass.setApplyTime(date);
                applyClass.setIsAgree(0); // 0代表未处理
                applyClassDao.save(applyClass);

                return Result.success("已提交申请请求");
            }
            return Result.fail(400, "班级不存在");
        } catch (UserNotFindException e) {
            logger.error(e.getMessage());
            return Result.fail(400 ,e.getMessage());
        }
    }

    // 学生端 获取班级下的题目列表
    @Override
    public Result getClassProblemList(Integer userId, Integer classId, PageAndSortDto page) {
        try {
            Result result = checkUserExist(userId);
            User user = (User) result.getData();

            Optional<Class> ojClass = classDao.findById(classId);
            if(ojClass.isPresent()) {
                List<Map<String, Object>> problems = null;
                Integer start = page.getPage();
                Integer limit = page.getLimit();

                if (user.getRole().equals(2)) {  // 注意此处roleId还需要协商
                    //普通用户只能查看该班级下题目  不管是不是班级的管理员或老师
                    problems = classDao.findClassProblems(userId, classId,(start - 1) * limit, limit);
                } else {
                    // 超级管理员可以查看全部题目
                    problems = classDao.findClassAllProblems(classId,(start - 1) * limit, limit);
                }
                // 获取额外信息
                List<Map<String, Object>> problems_with_extra_info = getProblemExtraInfo(problems, userId);

                return Result.success(problems_with_extra_info);
            } else {
                throw new OjClassNotFindException(classId);
            }
        } catch (UserNotFindException | OjClassNotFindException e) { // 如果用户或者班级不存在
            logger.error(e.getMessage());
            return Result.fail(400, e.getMessage());
        }

    }

    @Override
    public Result getClassProblemPageNum(Integer userId, Integer classId, Integer limit) {
        try {
            Result result = checkUserExist(userId);
            User user = (User) result.getData();
            Integer count;

            if(user.getRole().equals(2)) { // 普通用户
                count = classDao.getUserClassProblemCount(userId, classId);
            }else { // 超级管理员
                count = classDao.getAllClassProblemCount(classId);
            }
            Integer pageNumber;
            if (count % limit == 0) {
                pageNumber = count / limit;
            } else {
                pageNumber = count / limit + 1;
            }
            return Result.success(pageNumber);

        } catch (UserNotFindException e) {
            logger.error(e.getMessage());
            return Result.fail(400, e.getMessage());
        }
    }

    // 学生端 获取班级下的比赛
    @Override
    public Result getClassExam(Integer userId, Integer classId, PageAndSortDto page) {
        try {
            // 测试用的
            System.out.println("调用者ip: "+ InetAddress.getLocalHost().getHostAddress());
            checkUserExist(userId);

            Optional<Class> ojClass = classDao.findById(classId);
            if(ojClass.isPresent()) {
                Integer start = page.getPage();
                Integer limit = page.getLimit();

                List<Map<String, Object>> classExams = classDao.getClassExam(userId, classId, (start - 1) * limit, limit);

                return Result.success(classExams);
            } else {
                throw new OjClassNotFindException(classId);
            }
        } catch (UserNotFindException | OjClassNotFindException | UnknownHostException e) {
            logger.error(e.getMessage());
            return Result.fail(400, e.getMessage());
        }
    }

    @Override
    public Result getClassExamPageNum(Integer userId, Integer classId, Integer limit) {
        try {
            Result result = checkUserExist(userId);
            User user = (User) result.getData();
            Integer count;

            if(user.getRole().equals(2)) { // 普通用户
                count = classDao.getUserClassExamCount(userId, classId);
            }else { // 超级管理员
                count = classDao.getAllClassExamCount(classId);
            }
            Integer pageNumber;
            if (count % limit == 0) {
                pageNumber = count / limit;
            } else {
                pageNumber = count / limit + 1;
            }
            return Result.success(pageNumber);

        } catch (UserNotFindException e) {
            logger.error(e.getMessage());
            return Result.fail(400, e.getMessage());
        }
    }

    // 获取题目的额外信息 ac次数，总提交量啥的
    public List<Map<String, Object>> getProblemExtraInfo(List<Map<String, Object>> problems, Integer userId) {
        List<Map<String, Object>> problemsWithExtraInfo = new LinkedList<>();  // 新建一个list

        for (Map<String, Object> problem : problems) {
//                for(Map.Entry<String, Object> entry : problems.get(i).entrySet()) {
//                    System.out.print(" key = " + entry.getKey() + " value = " + entry.getValue());
//                }
//                System.out.println();
            Map<String, Object> extraInfo = new HashMap<>(problem);
            int problemId = (int) problem.get("id");
            List<Map<String, Object>> acceptSubmission = classDao.findAcceptSubmission(userId, problemId);
            if (acceptSubmission.size() > 0)
                extraInfo.put("status", 0);

            List<Map<String, Object>> attemptSubmission = classDao.findAttemptSubmission(userId, problemId);
            if (attemptSubmission.size() > 0)  // 有提交记录
                extraInfo.put("status", 1);
            else  // 没有提交记录
                extraInfo.put("status", 2);

            Map<String, Object> problemAcSubNum = classDao.findProblemAcSubNum(problemId);

            extraInfo.put("acNum", problemAcSubNum.get("ac_num"));
            extraInfo.put("acUserNum", problemAcSubNum.get("ac_user_num"));
            extraInfo.put("subNum", problemAcSubNum.get("sub_num"));
            extraInfo.put("subUserNum", problemAcSubNum.get("sub_user_num"));

            // 题目的标签数组暂时只返回一个标签, {tagName: xxx, count: 1}
            Map<String, Object> problemTags = classDao.findProblemTags(problemId);

            extraInfo.put("tags", problemTags);
            problemsWithExtraInfo.add(extraInfo);
        }
        return problemsWithExtraInfo;
    }

    @Override
    public Result getClassContest(Integer classId, PageAndSortDto page) {
        Integer page1 = page.getPage();
        Integer limit = page.getLimit();
        Integer start = (page1-1) * limit;
        List<Map<String, Object>> contests = classDao.getClassContest(classId, start, limit);
        List<Map<String,Object>> newList = new LinkedList<>();
        for(Map<String, Object> c : contests){
            Integer status;
            Timestamp now = new Timestamp(System.currentTimeMillis());
            Timestamp startTime = (Timestamp) c.get("startTime");
            Timestamp endTime = (Timestamp) c.get("endTime");
            if(now.before(startTime))
                status = 0;
            else if(now.before(endTime))
                status = 1;
            else
                status = 2;
            Map<String,Object> map = new HashMap<>(c);
            map.put("status",status);
            newList.add(map);
        }
        return Result.success(newList);
    }

    @Override
    public Result problemList(Integer classId, Integer userId, PageAndSortDto page) {
        Integer page1 = page.getPage();
        Integer limit = page.getLimit();
        Integer start = (page1-1)*limit;
        List<Map<String,Object>> list = classDao.findProblemList(classId,userId,start,limit);
        List<Map<String,Object>> newList = new LinkedList<>();
        for(Map<String,Object> p: list){
            //从数据库获取的是BigInteger类型，不能直接转成Integer
            Integer sub = Integer.valueOf(p.get("isSub").toString());
            Integer ac = Integer.valueOf(p.get("isAc").toString());
            Integer status = null;
            if(ac > 0)
                status = 0;
            else if(sub > 0)
                status = 1;
            else
                status = 2;
            Map<String,Object> map = new HashMap<>(p);
            map.remove("isSub");
            map.remove("isAc");
            map.put("status",status);
            newList.add(map);
        }
        return Result.success(newList);
    }

    private Result checkUserExist(Integer userId) throws UserNotFindException {
        Result result = RestUtil.get(restTemplate, userServiceUrl + "/user/" + userId, User.class);
        if (!result.getStatus().equals(200)) {
            throw new UserNotFindException(userId);
        }
        return result;
    }

}
