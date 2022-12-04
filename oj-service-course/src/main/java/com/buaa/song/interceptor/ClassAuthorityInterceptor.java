package com.buaa.song.interceptor;

import com.buaa.song.dao.ClassDao;
import com.buaa.song.dao.MembershipDao;
import com.buaa.song.entity.Class;
import com.buaa.song.entity.Membership;
import com.buaa.song.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

/**
 * @FileName: ClassAuthorityInterceptor
 * @author: ProgrammerZhao
 * @Date: 2021/11/17
 * @Description:
 */
@Component
public class ClassAuthorityInterceptor implements HandlerInterceptor {

    @Autowired
    private ClassDao classDao;
    @Autowired
    private MembershipDao membershipDao;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        Integer classId = Integer.valueOf((String)pathVariables.get("id"));
        Optional<Class> optional = classDao.findById(classId);
        if(optional.isPresent()){
            Class aClass = optional.get();
            String access = aClass.getAccess();
            if(access.equals("public")){
                return true;
            }else if(access.equals("protected")){
                String token = request.getHeader("authorization");
                if(token != null){
                    Integer userId = JwtUtil.decode(token).get("id", Integer.class);
                    System.out.println("userID: " + userId+" classID: "+classId);
                    Membership m = membershipDao.findByUserIdAndClassId(userId, classId);
                    if(m != null){
                        System.out.println("YES YES YES");
                        return true;
                    }
                    System.out.println("S H I T! THREE FREEZE!");
                }
            }
        }
        response.sendError(401);
        return false;
    }
}
