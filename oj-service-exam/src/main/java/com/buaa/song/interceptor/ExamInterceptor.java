package com.buaa.song.interceptor;

import com.buaa.song.dao.ExamDao;
import com.buaa.song.entity.Exam;
import com.buaa.song.entity.Membership;
import com.buaa.song.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;

/**
 * @FileName: ExamInterceptor
 * @author: ProgrammerZhao
 * @Date: 2021/11/17
 * @Description:
 */
@Component
public class ExamInterceptor implements HandlerInterceptor {

    @Autowired
    private ExamDao examDao;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        Integer examId = Integer.valueOf((String)pathVariables.get("id"));
        Optional<Exam> optional = examDao.findById(examId);
        if(optional.isPresent()){
            Exam exam = optional.get();
            String access = exam.getAccess();
            if(access.equals("public")){
                return true;
            }else if(access.equals("protected")){
                String token = request.getHeader("authorization");
                if(token != null){
                    Integer userId = JwtUtil.decode(token).get("id", Integer.class);
                    Integer classId = exam.getClassId();
                    BigInteger b = examDao.hasPermission(userId, classId);
                    if(b.intValue() > 0){
                        return true;
                    }
                }
            }
        }
        response.sendError(401);
        return false;
    }
}
