package com.buaa.song.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.buaa.song.dao.ApplyClassDao;
import com.buaa.song.dao.ClassDao;
import com.buaa.song.dao.ClassProblemDao;
import com.buaa.song.dao.MembershipDao;
import com.buaa.song.dto.ImportUserDto;
import com.buaa.song.dto.PageAndSortDto;
import com.buaa.song.entity.*;
import com.buaa.song.entity.Class;
import com.buaa.song.exception.ExcelFormatException;
import com.buaa.song.exception.OjClassNotFindException;
import com.buaa.song.exception.UserNotFindException;
import com.buaa.song.result.Result;
import com.buaa.song.service.ClassAdminService;

import com.buaa.song.utils.RestUtil;
import com.buaa.song.utils.excelUtils.ExcelReadListener;
import com.buaa.song.utils.excelUtils.ExcelUtil;
import com.buaa.song.utils.model.ExcelReadModel;
import com.buaa.song.utils.model.ExcelWriteModel;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.*;


@Service
@RefreshScope
@Slf4j
public class ClassAdminServiceImpl implements ClassAdminService {
    // logger是记录日志吧
    private static final Logger logger = LoggerFactory.getLogger(ClassAdminServiceImpl.class);

    @Autowired
    private ClassDao classDao;

    @Autowired
    private ClassProblemDao classProblemDao;

    @Autowired
    private MembershipDao membershipDao;

    @Autowired
    private ApplyClassDao applyClassDao;

    @Autowired
    private RestTemplate restTemplate;

    private static final String userServiceUrl = "http://oj-service-user";

    private static final Integer PUBLIC_ACCESS = 0;
    private static final Integer PROTECETD_ACCESS = 1;
    private static final Integer PRIVATE_ACCESS = 2;

    private static final Integer CLASS_MEMBER = 0;
    private static final Integer CLASS_ADMIN = 1;
    private static final Integer CLASS_CREATOR = 2;

    //    @Value("${file.excel}")
    private String excelFilePath;


    // 应该是赵博学长的古老代码，需要修改
    @Override
    public Result findClassByUserId(Integer id) {
        try {
            Result result = restTemplate.getForObject(userServiceUrl + "/user/" + id, Result.class);
            User user;
            if (result.getStatus().equals(200)) {
                user = BeanUtil.fillBeanWithMap((Map) result.getData(), new User(), false);
                Integer roleId = user.getRole();
                List<Map<String,Object>> list;
                if (roleId.equals(2)) {
                    //普通教师，只能查看自己创建和管理的课程
                    list = classDao.findMyClass(id);
                } else {
                    list = classDao.findAllClass();
                }
                List<Map<String,Object>> classs = new ArrayList<>();
                for(Map<String,Object> elem : list){
                    Map<String,Object> map = new HashMap<>();
                    map.putAll(elem);
                    Integer classId = (Integer) map.get("id");
                    BigInteger memberNum = classDao.getClassMemberNum(classId);
                    map.put("memberNum",memberNum);
                    classs.add(map);
                }
                return Result.success(classs);
            } else {
                throw new UserNotFindException(id);
            }
        } catch (UserNotFindException e) {
            logger.error(e.getMessage());
            return Result.fail(404, e.getMessage());
        }

    }

    @Transactional
    @Override
    public Result addClass(Integer userId, Class c) {
        try {
            Result result = checkUserExist(userId);
            User user = (User) result.getData();

            if(user.getRole().equals(3)){ // 超级管理员才能添加班级
                c.setCreator(userId);
                c.setCreateTime(new Date());
                 Class newClass = classDao.save(c);

                 Membership member  = new Membership(userId, newClass.getId(), "creator"
                         , new Date(System.currentTimeMillis()));
                 membershipDao.save(member);

                return Result.success(c);
            } else {                      // 非超级管理员无权操作
                return Result.fail(400, "用户无权进行操作");
            }
        } catch (UserNotFindException e) {
            logger.error(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(400, e.getMessage());
        }
    }


    @Transactional
    @Override
    public Result deleteClass(Integer userId, Integer classId) {
        try {
            Result result = checkUserExist(userId);
            User user = (User) result.getData();

            Optional<Class> ojClass = classDao.findById(classId);
            if(ojClass.isPresent()) {
                if(user.getRole().equals(3)) { // 超级管理员才能删除班级
                    classDao.deleteById(classId);
                    return Result.success("删除班级成功");
                } else {                       // 非超级管理员无权操作
                    return Result.fail(400, "用户无权进行操作");
                }
            } else {
                throw new OjClassNotFindException(classId);
            }
        } catch (UserNotFindException | OjClassNotFindException e) {
            logger.error(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(400, e.getMessage());
        }
    }

    public Result findClassById(Integer classId) {
        Optional<Class> ojClass = classDao.findById(classId);
        if (ojClass.isPresent()) {
            return Result.success("查询成功", ojClass.get());
        }
        return Result.fail(400, "该课程不存在");
    }

    // 需要解决class-problem表中的遗留问题，需要加上access字段
    @Override
    public Result setProblemAccess(Integer classId, Integer problemId, Integer access) {
        ClassProblem classProblem = classProblemDao.findClassProblem(classId, problemId);
        if(classProblem == null) {
            return Result.fail(400, "班级题目不存在");
        } else {
            if(access.equals(PUBLIC_ACCESS)) {
                classProblem.setAccess("public");
            } else if(access.equals(PROTECETD_ACCESS)) {
                classProblem.setAccess("protected");
            } else if(access.equals(PRIVATE_ACCESS)) {
                classProblem.setAccess("private");
            } else {
                return Result.fail(400, "题目权限错误");
            }
            classProblemDao.save(classProblem);
            return Result.success("权限设置成功");
        }
    }

    @Override
    public Result setUserAccess(Integer classId, Integer userId, Integer access) {
        Membership membership = membershipDao.findByUserIdAndClassId(userId, classId);
        if(membership != null) {
            if(access.equals(CLASS_MEMBER)) {
                membership.setType("member");
            } else if(access.equals(CLASS_ADMIN)){
                membership.setType("admin");
            } else if(access.equals(CLASS_CREATOR)) {
                membership.setType("creator");
            } else {
                return Result.fail(400, "用户权限错误");
            }
            membershipDao.save(membership);
            return Result.success("用户权限设置成功");
        }
        return Result.fail(400, "班级成员不存在");
    }

    @Override
    public Result getAdminClassInfo(Integer classId) {
        try {
            Result result = findClassById(classId);  // 检查班级是否存在
            Integer status = result.getStatus();
            if(!status.equals(200)) {
                throw new OjClassNotFindException(classId);
            }
            Map<String, Object> classInfo = classDao.getAdminClassInfo(classId);

            return Result.success(classInfo);
        } catch (OjClassNotFindException e) {
            logger.error(e.getMessage());
            return Result.fail(400, e.getMessage());
        }
    }

    @Transactional
    @Override
    public Result updateClassInfo(Integer classId, Class clazz) {
        try {
            Optional<Class> ojClass = classDao.findById(classId);
            if(ojClass.isPresent()) {
                clazz.setId(classId);
                Class newClass = classDao.save(clazz);
                return Result.success(newClass);
            } else {
                throw new OjClassNotFindException(classId);
            }
        } catch (OjClassNotFindException e) {
            logger.error(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(400, e.getMessage());
        }
    }

    @Override
    public Result getClassMember(Integer classId) {
        List<Map<String, Object>> members = classDao.getClassStudent(classId);
        return Result.success(members);
    }

    @Override
    public Result getUserInfo(Integer cid, Integer uid) {
        List<Map<String, Object>> userInfo = classDao.getUserInfoOfClass(cid, uid);
        return Result.success(userInfo);
    }

    @Override
    public Result getAdminProblemList(Integer classId, PageAndSortDto page) {
        Integer start = page.getPage();
        Integer limit = page.getLimit();
        List<Map<String, Object>> problemList = classDao.findClassAllProblems(classId, (start - 1) * limit, limit);

        // 获取额外信息
        List<Map<String, Object>> problems_with_extra_info = getAdminProblemExtraInfo(problemList);
        return Result.success(problems_with_extra_info);
    }

    // 管理端获取题目的额外信息 与用户端区别在于不用传入userId
    public List<Map<String, Object>> getAdminProblemExtraInfo(List<Map<String, Object>> problems) {
        List<Map<String, Object>> problemsWithExtraInfo = new LinkedList<>();  // 新建一个list

        for (Map<String, Object> problem : problems) {
//                for(Map.Entry<String, Object> entry : problems.get(i).entrySet()) {
//                    System.out.print(" key = " + entry.getKey() + " value = " + entry.getValue());
//                }
//                System.out.println();
            Map<String, Object> extraInfo = new HashMap<>(problem);
            int problemId = (int) problem.get("id");

            Map<String, Object> problemAcSubNum = classDao.findProblemAcSubNum(problemId);

            extraInfo.put("acNum", problemAcSubNum.get("ac_num"));
            extraInfo.put("acUserNum", problemAcSubNum.get("ac_user_num"));
            extraInfo.put("subNum", problemAcSubNum.get("sub_num"));
            extraInfo.put("subUserNum", problemAcSubNum.get("sub_user_num"));

            extraInfo.put("tags", new int[0]); // 题目的标签数组暂定为空数组
            problemsWithExtraInfo.add(extraInfo);
        }
        return problemsWithExtraInfo;
    }

    @Override
    public Result getAdminClassExams(Integer classId, PageAndSortDto page) {
        Integer start = page.getPage();
        Integer limit = page.getLimit();

        List<Map<String, Object>> examList = classDao.getAdminClassExams(classId, (start - 1) * limit, limit);
        return Result.success(examList);
    }

    @Override
    public Result setUserToAdmin(Integer cid, Integer uid) {
        Membership member = membershipDao.findByUserIdAndClassId(uid, cid);
        if (member.getType().equals("member")) {
            member.setType("admin");
            membershipDao.save(member);
            return Result.success();
        } else {
            return Result.fail(400);
        }
    }

    @Override
    public Result setUserToMember(Integer cid, Integer uid) {
        Membership member = membershipDao.findByUserIdAndClassId(uid, cid);
        if (member.getType().equals("admin")) {
            member.setType("member");
            membershipDao.save(member);
            return Result.success();
        } else {
            return Result.fail(400);
        }
    }

    @Transactional
    @Override
    public Result deleteUser(Integer cid, Integer uid) {
        membershipDao.deleteByUserIdAndClassId(uid, cid);
        return Result.success("移除用户成功");
    }

    @Override
    public Result addUserFromExcel(MultipartFile file, Integer classId, String suffix, Integer pattern,
                                   String password) {
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            ExcelReadListener listener = ExcelUtil.readExcelFile(inputStream);
            //检查excel表格格式是否正确
            ExcelUtil.checkExcelFile(listener);
            //获取excel表格的数据
            List<ExcelReadModel> datas = listener.getData();
            List<ExcelWriteModel> writeData = null;
            //根据模式导入数据
            switch (pattern) {
                case 1:
                    writeData = insertDataByModelOne(datas, classId, suffix);
            }
            //将导入结果记录在excel中
            String fileName = ExcelUtil.writeExcelFile(writeData, excelFilePath);
            return Result.success(null, fileName);
        } catch (ExcelFormatException e) {
            e.printStackTrace();
            return Result.fail(400, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail(500, e.getMessage());
        }
    }

    private List<ExcelWriteModel> insertDataByModelOne(List<ExcelReadModel> datas, Integer classId, String suffix) {
        List<ExcelWriteModel> excelWriteModels = new ArrayList<>();

        for (ExcelReadModel data : datas) {
            String studentId = data.getStudentId();
            String name = data.getName();
            Result result = restTemplate.postForObject(userServiceUrl + "/user/excel/import?"
                    + "studentId=" + studentId + "&name=" + name + "&suffix=" + suffix, null, Result.class);
            if (result.getStatus().equals(200)) {
                ImportUserDto importUserDto = BeanUtil.fillBeanWithMap((Map) result.getData(), new ImportUserDto(), false);
                ExcelWriteModel model = importUserDto.getModel();
                excelWriteModels.add(model);
                Integer userId = importUserDto.getUserId();
                Membership byUserIdAndClassId = membershipDao.findByUserIdAndClassId(userId, classId);
                if (byUserIdAndClassId == null) {
                    membershipDao.save(new Membership(userId, classId, "member", new Date(System.currentTimeMillis())));
                }
            }
        }

        return excelWriteModels;
    }

    @Override
    public Result addUser(Integer classId, String way, String content) {
        if (way.equals("username")) {
            String username = content;
            User user;
            String url = userServiceUrl + "/isUsernameExist?username=" + username;
            Result result = restTemplate.getForObject(url, Result.class);
            if (result.getStatus().equals(404)) {
                return Result.fail(404, "该用户不存在");
            } else {
                user = BeanUtil.fillBeanWithMap((Map) result.getData(), new User(), false);

                return attemptToAddUserIntoMembership(user.getId(), classId);
            }
        } else {
            String studentId = content;
            String url = userServiceUrl + "/user/auth?studentId=" + studentId;
            Result result = restTemplate.getForObject(url, Result.class);
            Authentication auth = BeanUtil.fillBeanWithMap((Map) result.getData(), new Authentication(), false);
            if (auth == null) {
                return Result.fail(400, "该学号没有绑定账号");
            } else {
                Integer userId = auth.getUserId();

                return attemptToAddUserIntoMembership(userId, classId);
            }
        }
    }

    private Result attemptToAddUserIntoMembership(Integer userId, Integer classId) {
        Membership member = membershipDao.findByUserIdAndClassId(userId, classId);
        if (member == null) {
            member = new Membership(userId, classId, "member", new Date(System.currentTimeMillis()));
            membershipDao.save(member);
            return Result.success("添加成功");
        } else {
            return Result.fail(400, "该用户已加入课程");
        }
    }

    @Override
    public Result getApplyUsers(Integer classId) {
        List<Map<String, Object>> allUser = applyClassDao.findAllUser(classId);
        return Result.success(allUser);
    }

    @Override
    public Result dealApplyUser(Integer applyId, Integer isAgree, Integer dealPerson) {
        Optional<ApplyClass> optionalApplyClass = applyClassDao.findById(applyId);
        if (optionalApplyClass.isPresent()) {
            ApplyClass applyClass = optionalApplyClass.get();
            applyClass.setIsAgree(isAgree);
            applyClass.setDealPerson(dealPerson);
            applyClass.setDealTime(new Date(System.currentTimeMillis()));
            applyClassDao.save(applyClass);
            if (isAgree.equals(1)) {
                Integer userId = applyClass.getUserId();
                Integer classId = applyClass.getClassId();
                Membership member = membershipDao.findByUserIdAndClassId(userId, classId);
                if (member == null) {
                    member = new Membership(userId, classId, "member", new Date(System.currentTimeMillis()));
                    membershipDao.save(member);
                    return Result.success("处理成功,已同意申请");
                } else {
                    return Result.fail(400, "该用户已在课程中");
                }
            }
            return Result.success("处理成功，已拒绝申请");
        }
        return Result.fail(400, "该申请不存在");
    }

    @Override
    public String downloadExcelFile(String fileName, HttpServletResponse response) {
        return null;
    }

    private Result checkUserExist(Integer userId) throws UserNotFindException {
        Result result = RestUtil.get(restTemplate, userServiceUrl + "/user/" + userId, User.class);
        if (!result.getStatus().equals(200)) {
            throw new UserNotFindException(userId);
        }
        return result;
    }
}
