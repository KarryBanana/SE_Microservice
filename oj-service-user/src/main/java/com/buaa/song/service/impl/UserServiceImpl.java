package com.buaa.song.service.impl;

import com.buaa.song.dao.AuthenticationDao;
import com.buaa.song.dao.RegisterDao;
import com.buaa.song.dao.UserDao;
import com.buaa.song.dao.VerificationCodeDao;
import com.buaa.song.dto.*;
import com.buaa.song.entity.Authentication;
import com.buaa.song.entity.Submission;
import com.buaa.song.entity.User;
import com.buaa.song.entity.VerificationCode;
import com.buaa.song.exception.UserNotFindException;
import com.buaa.song.result.Result;
import com.buaa.song.service.UserService;
import com.buaa.song.utils.JwtUtil;
import com.buaa.song.utils.PasswordUtil;
import com.buaa.song.utils.StringUtil;
import com.buaa.song.utils.model.ExcelWriteModel;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import javax.swing.text.html.Option;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

/**
 * @FileName: UserServiceImpl
 * @author: ProgrammerZhao
 * @Date: 2020/10/24
 * @Description:
 */

@Service
@RefreshScope
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private AuthenticationDao authenticationDao;
    @Autowired
    private VerificationCodeDao verificationCodeDao;
    @Autowired
    private MailSender mailSender;

    // ckr测试gateway
    @Resource
    Environment environment;

    private final boolean superAdmin = true;

    private static final Map<Integer, String> PRIVACY_TYPE = new HashMap<Integer, String>(){{
        put(0, "public");
        put(1, "protected");
        put(2, "private");
    }};

    private static final Integer PRIVACY_PUBLIC = 0;
    private static final Integer PRIVACY_PROTECTED = 1;
    private static final Integer PRIVACY_PRIVATE = 2;

    private enum SubmissionResult {
        AC("AC"), CE("CE"), WA("WA"), PE("PE"),  TLE("TLE"),
        OE("OE");

        private String name;

        private SubmissionResult(String name) {this.name = name;}

        public String getName() {
            return name;
        }
    }

    private static final int codeLength = 6;
    private static final int codeExpireTimeMills = 5 * 60 * 1000;
    @Value("${spring.mail.username}")
    private String from;

    @Override
    public Result register(RegisterDto dto) {
        String username = dto.getUsername();
        User byUsername = userDao.findByUsername(username);
        if(byUsername == null){
            User user = dto.transferToUser();

            Date date = new Date(System.currentTimeMillis());
            user.setRole(2);
            user.setCreateTime(date);
            user.setUpdateTime(date);
            userDao.save(user);
            return Result.success("注册成功");
        }else {
            return Result.fail(400,"用户名已存在");
        }
    }

    @Override
    public Result editVisit(Integer userId, Integer privacy){
        try {
            Optional<User> optional = userDao.findById(userId);
            optional.orElseThrow(() -> new UserNotFindException(userId));

            User user = optional.get();

            if (!PRIVACY_TYPE.containsKey(privacy))
                return Result.fail(400, "用户隐私类型错误");

            String privacyType = PRIVACY_TYPE.get(privacy);
            user.setPrivacy(privacyType);
            userDao.save(user);

            return Result.success("用户隐私权限设置成功");
        } catch (UserNotFindException e) {
            log.error(e.getMessage());
            return Result.fail(400, "用户不存在");
        }
    }

    @Override
    public Result isUsernameExist(String username) {
        User user = userDao.findByUsername(username);
        if (user != null) {
            return Result.success("用户名已存在", user);
        } else {
            return Result.fail(404, "用户名不存在");
        }
    }

    @Override
    public Result isSuperAdmin(Integer userId) {
        try {
            Optional<User> optional = userDao.findById(userId);
            if(optional.isPresent()) {
                User user = optional.get();
                Integer role = user.getRole();
                if(role.equals(3)) {
                    return Result.success(superAdmin);
                } else {
                    return Result.success(!superAdmin);
                }
            } else {
                throw new UserNotFindException(userId);
            }
        } catch (UserNotFindException e) {
            log.error(e.getMessage());
            return Result.fail(400, "用户不存在");
        }
    }

    @Override
    public Result findUserById(Integer id) {
        try {
            // 2022 暑假为了测试微服务架构加入显示ip的代码
            System.out.println("from server ip: " + InetAddress.getLocalHost().getHostAddress());

            Optional<User> user = userDao.findById(id);
            user.orElseThrow( () -> new UserNotFindException(id));

            return Result.success("查询成功", user.get());

        } catch (UserNotFindException e) {
            return Result.fail(400, "查询失败");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return Result.fail(400, "获取服务ip失败");
        }
    }

    @Override
    public Result resetPassword(String token, ResetPasswordDto dto) {
        if(token == null){
            return Result.fail(400,"请求未携带token");
        }
        if(!JwtUtil.isVerify(token)){
            return Result.fail(400,"无效的Token");
        }
        Claims claims = JwtUtil.decode(token);
        String usage = (String) claims.get("usage");
        if(!usage.equals("reset password")){
            return Result.fail(400,"无效的Token");
        }

        Integer id = dto.getId();
        String password = dto.getPassword();
        Optional<VerificationCode> optionalVerificationCode = verificationCodeDao.findById(id);
        if(optionalVerificationCode.isPresent()){
            VerificationCode verificationCode = optionalVerificationCode.get();
            String email = verificationCode.getEmail();
            Integer result = userDao.updatePassword(email, PasswordUtil.generate(password));
            if(result > 0){
                return Result.success("重置密码成功");
            }else {
                return Result.fail(500,"修改密码失败");
            }
        }
        return Result.fail(500,"信息丢失");
    }

    @Override
    public Result importUserByExcel(String studentId, String name, String suffix) {
        Integer userId;
        ExcelWriteModel model = new ExcelWriteModel();
        model.setName(name);
        model.setStudentId(studentId);
        Authentication byStudentId = authenticationDao.findByStudentId(studentId);
        if (byStudentId == null) {
            model.setIsAuth("否");
            String username = studentId + suffix;
            User byUsername = userDao.findByUsername(username);
            if (byUsername == null) {
                //用户名和学号都不存在则新建账号并加入课程
                userId = saveUser(username, studentId, name);
                saveAuth(userId, studentId, name);
                model.setUsername(username);
                model.setPassword(studentId);
            } else {
                userId = byUsername.getId();
                Authentication byUserId = authenticationDao.findByUserId(userId);
                if (byUserId == null) {
                    //学号不存在且用户名已存在，用户名未绑定账号，默认该账号与学号为同一人，直接绑定
                    saveAuth(userId, studentId, name);
                    model.setUsername(username);
                    model.setRemark("该账号之前已注册，如果不是本人的账号，请联系管理员");
                } else {
                    //学号不存在，用户名存在却绑定了其他账号，则在学号后面加四位随机字母生成另一个账号
                    while (true) {
                        username = studentId + StringUtil.getRandomString(4) + suffix;
                        User byUsername1 = userDao.findByUsername(username);
                        if (byUsername1 == null)
                            break;
                    }
                    userId = saveUser(username, studentId, name);
                    saveAuth(userId, studentId, name);
                    model.setUsername(username);
                    model.setPassword(studentId);
                    model.setRemark("账号" + studentId + suffix + "已绑定其它学号");
                }
            }
        } else {
            //学号已存在，直接将学号绑定的账号加入课程中
            userId = byStudentId.getUserId();
            Optional<User> byId = userDao.findById(userId);
            if(byId.isPresent()){
                User user = byId.get();
                model.setUsername(user.getUsername());
                model.setRemark("该学号已绑定账号，如果账号不是本人的，请联系管理员");
            }
        }
        ImportUserDto importUserDto = new ImportUserDto(userId, model);
        return Result.success(importUserDto);
    }

    @Override
    public Result login(UserLoginDto dto) {
        String username = dto.getUsername();
        String password = dto.getPassword();
        try {
            User user = userDao.findByUsername(username);

            if(user == null){
                return Result.fail(403,"用户名错误");
            }else{
                String passwordInSql = user.getPassword();
                if(PasswordUtil.verify(password, passwordInSql)){
                    Date date = new Date(System.currentTimeMillis());
                    user.setLastLogin(date);
                    userDao.save(user);
                    Map<String, Object> claims = new HashMap<>();
                    claims.put("id",user.getId());
                    String token = JwtUtil.encode(username, claims);

                    Map<String, Object> info = new HashMap<>();
                    info.put("token", token);
                    info.put("uid", user.getId());

                    return Result.success("登录成功",info);
                }else{
                    return Result.fail(403,"密码错误");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(500,"系统错误");
        }
    }

    @Override
    public Result sendCode(String email) {
        User byUsername = userDao.findByUsername(email);
        if(byUsername == null){
            return Result.fail(400,"该用户不存在");
        }
        String code = StringUtil.getRandomString(codeLength);
        Date expireTime = new Date(System.currentTimeMillis() + codeExpireTimeMills);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(email);
            message.setSubject("OJ认证验证码");
            message.setText("验证码是"+code+",有效期为5分钟");
            mailSender.send(message);
            log.info("验证码发送成功，mail:"+email+",code:"+code);
        } catch (Exception e) {
            log.error(e.getMessage());
            return Result.fail(400,e.getMessage());
        }

        // 保存验证码
        VerificationCode verificationCode = new VerificationCode(email,code,expireTime);
        VerificationCode savedVerifi = verificationCodeDao.save(verificationCode);

        return Result.success(savedVerifi.getId());
    }

    @Override
    public Result verificateCode(VerificateCodeDto dto) {
        Integer id = dto.getId();
        String email = dto.getEmail();
        String code = dto.getCode();
        Optional<VerificationCode> byId = verificationCodeDao.findById(id);
        if(!byId.isPresent()){
            return Result.fail(400,"验证信息丢失");
        }
        // 数据库中拿到的code
        VerificationCode verificationCode = byId.get();

        String mail1 = verificationCode.getEmail();
        if(!mail1.equals(email)){
            return Result.fail(400,"两次输入的邮箱不一样");
        }

        Date expireTime = verificationCode.getExpireTime();
        if(new Date(System.currentTimeMillis()).after(expireTime)){
            return Result.fail(400,"验证码已过期");
        }

        String code1 = verificationCode.getCode();
        if(!code1.equals(code)){
            return Result.fail(400,"验证码错误");
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("usage","reset password");
        String token = JwtUtil.encode(email, claims);
        return Result.success(null,token);
    }

    @Override
    public Result logout(String token) {
        return null;
    }

    @Override
    public Result getInfo(Integer userId) {
        Map<String, Object> info = userDao.getInfoById(userId);
        return Result.success(info);
    }

    @Override
    public Result getUserPrivacy(Integer userId) {
        try {
            Optional<User> optional = userDao.findById(userId);
            if(optional.isPresent()){
                User user = optional.get();
                String privacy = user.getPrivacy();

                return Result.success("获取隐私权限成功!",privacy); // 这里直接返回privacy会把值传入到msg中
            } else {
                throw new UserNotFindException(userId);
            }
        } catch (UserNotFindException e) {
            log.error(e.getMessage());
            return Result.fail(400, "用户不存在");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result editInfo(Integer userId, EditInfoDto dto) {
        try {
            Optional<User> optional = userDao.findById(userId);
            if(optional.isPresent()){
                User user = optional.get();
                user.setNickname(dto.getNickname())
                        .setDescription(dto.getDescription())
                        .setSchool(dto.getSchool())
                        .setMajor(dto.getMajor())
                        .setStudentId(dto.getStudentId())
                        .setRealName(dto.getRealname());
                userDao.save(user);
                return Result.success();
            }else {
                throw new UserNotFindException(userId);
            }
        } catch (UserNotFindException e) {
            log.error(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(400,"用户不存在");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updatePassword(Integer userId, UpdatePasswordDto dto) {
        try {
            Optional<User> optional = userDao.findById(userId);
            if(optional.isPresent()){
                User user = optional.get();
                String encPassword = user.getPassword();
                String oldPassword = dto.getOldPassword();
                if(!PasswordUtil.verify(oldPassword,encPassword)){
                    return Result.fail(400,"原密码错误");
                }
                String newPassword = dto.getNewPassword();
                String newPasswordEnc = PasswordUtil.generate(newPassword);
                user.setPassword(newPasswordEnc);
                userDao.save(user);
                return Result.success();
            }else {
                throw new UserNotFindException(userId);
            }
        } catch (UserNotFindException e) {
            log.error(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(400,"用户不存在");
        }
    }

    @Override
    public Result getAnnualSubmission(Integer userId) {
        try {
            Optional<User> option = userDao.findById(userId);
            if(option.isPresent()) {
                int year = LocalDate.now().getYear();
                BigInteger[] annualSubmit;
                // 判断闰年
                if(year % 400 == 0 || (year % 4 == 0 && year % 100 != 0))
                    annualSubmit = new BigInteger[366];
                else
                    annualSubmit = new BigInteger[365];

                Arrays.fill(annualSubmit, BigInteger.valueOf(0));

                LocalDate firstDateOfYear = LocalDate.of(year, Month.JANUARY, 1);
                // 距离该年1月1号过去了多少天
                long pastDays = LocalDate.now().toEpochDay() - firstDateOfYear.toEpochDay();
                System.out.println(pastDays);

                List<Map<String, Object> > annualData = userDao.getAnnualSubmission(userId, pastDays);
                /*
                返回subTimes为某天的提交次数，daysBefore代表该天距离今天的日期差
                annualSubmit[pastDays - daysBefore] = subTimes
                意思为：例如今天2021-11-14取得的数据为 10 317
                已知2021-01-01与2021-11-14相差317天，则annualSubmit[317 - 317] = 10
                 */

                for (Map<String, Object> data : annualData) {
                    int daysBefore = (int) data.get("daysBefore");
                    BigInteger subTimes = (BigInteger) data.get("subTimes");

                    annualSubmit[(int) (pastDays - daysBefore) ] = subTimes;
                }

                Map<String, Object> res = new HashMap<>();
                res.put("submission", annualSubmit);
                res.put("year", year);

                return Result.success(res);
            } else {
                throw new UserNotFindException(userId);
            }
        } catch (UserNotFindException e) {
            log.error(e.getMessage());
            return Result.fail(400, "用户不存在");
        }
    }

    @Override
    public  Result getAllSubmissionType(Integer userId) {
        Optional<User> user = userDao.findById(userId);
        if(user.isPresent()) {
            List<Map<String, Object>> submission_type = userDao.getAllSubmissionType(userId);
            Map<String, BigInteger> stat = new HashMap<String, BigInteger>();

            for (SubmissionResult sr : SubmissionResult.values()) {
                stat.put(sr.getName(), BigInteger.valueOf(0));
            }

            for(Map<String, Object> st : submission_type) {
                String result_name = (String) st.get("result"); // 提交结果的名称
                for(SubmissionResult sr : SubmissionResult.values()) {
                    if(sr.getName().equals(result_name)) {
                        stat.put(sr.getName(), (BigInteger) st.get("number")); // 该类提交结果的总次数
                        break;
                    }
                }
            }
            return Result.success(stat);

        } else {
            return Result.fail(400, "用户不存在");
        }
    }

    @Override
    public Result getWeekSubmission(Integer userId) {
        Optional<User> user = userDao.findById(userId);
        if(user.isPresent()) {

            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            // 周日算一周的第一天，即周日的idx为0，以此类推
            int dayNumberOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
            System.out.println("今天是本周的第"+ dayNumberOfWeek + "天");

            List<List<BigInteger>> weekSubmission = new ArrayList<List<BigInteger>>();

            for(int i = dayNumberOfWeek; i>=0; --i) { // 比如今天为周三，从今天往前倒退，统计每天的提交情况
                List<BigInteger> current_day_sub = new ArrayList<BigInteger>();
                // 获取该天每一种结果的次数
                List<Map<String, Object> > daySubmissions = userDao.getWeekSubmission(userId, i);

                // 根据前端的约定获取到每种提交结果类型的次数
                for (Map<String, Object> d : daySubmissions) {
                    current_day_sub.add((BigInteger) d.get("number"));
                }
                weekSubmission.add(current_day_sub);
            }

            for(int i = dayNumberOfWeek + 1; i < 7; ++i) {
                List<BigInteger> list = new ArrayList<BigInteger>();
                for(int j = 1; j<=6; ++j) {
                    list.add(BigInteger.valueOf(0));
                }
                weekSubmission.add(list);
            }

            return Result.success(weekSubmission);

        } else {
            return Result.fail(400, "用户不存在");
        }
    }

    @Override
    public Result getLatestSubmission(Integer userId) {
        Optional<User> user = userDao.findById(userId);
        if(user.isPresent()) {
            List<Map<String, Object>> latest_sub = userDao.getLatestSubmission(userId);

            return Result.success(latest_sub);
        } else {
            return Result.fail(400, "用户不存在");
        }
    }

    @Override
    public Result getUserSchools(){
        List<String > schoolsList = userDao.getUserSchools();
        // System.out.println(schoolsList);

        return Result.success(schoolsList);
    }

    private void saveAuth(Integer userId, String studentId, String name) {
        Authentication auth = new Authentication(userId, studentId, name);
        authenticationDao.save(auth);
    }

    private Integer saveUser(String username, String studentId, String name) {
        Date date = new Date(System.currentTimeMillis());
        User user = new User(username, PasswordUtil.generate(studentId), 1, name, date, date);
        User savedUser = userDao.save(user);
        return savedUser.getId();
    }
}