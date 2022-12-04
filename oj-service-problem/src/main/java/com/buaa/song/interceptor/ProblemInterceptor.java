package com.buaa.song.interceptor;

import com.buaa.song.dao.ProblemDao;
import com.buaa.song.entity.Membership;
import com.buaa.song.entity.Problem;
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
 * @FileName: ProblemInterceptor
 * @author: ProgrammerZhao
 * @Date: 2021/11/17
 * @Description:
 */
@Component
public class ProblemInterceptor implements HandlerInterceptor {

    @Autowired
    private ProblemDao problemDao;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        Integer problemId = Integer.valueOf((String) pathVariables.get("id"));
        Optional<Problem> optional = problemDao.findById(problemId);
        if(optional.isPresent()){
            Problem problem = optional.get();


            return true;
//            String access = problem.getAccess();
//            if(access.equals("public")){
//                return true;
//            }else if(access.equals("protect")){
//                String token = request.getHeader("authorization");
//                if(token != null) {
//                    Integer userId = JwtUtil.decode(token).get("id", Integer.class);
//                    BigInteger b = problemDao.hasProblemPermission(problemId, userId);
//                    if(b.intValue() > 0){
//                        return true;
//                    }
//                }
//            }
        }
        response.sendError(401);
        return false;
    }


}
