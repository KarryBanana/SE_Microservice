package com.buaa.song.interceptor;

import com.buaa.song.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @FileName: LoginInterceptor
 * @author: ProgrammerZhao
 * @Date: 2021/4/28
 * @Description:
 */
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
//        String token = request.getHeader("Authorization");
//        if(token == null || token.equals(""))
//            return false;
//        Claims claims = JwtUtil.decode(token);
//        Integer userId = (Integer) claims.get("user_id");
//        Integer examId = (Integer) claims.get("exam_id");
//        if(JwtUtil.isVerify(token) && userId != null && examId != null){
//            String uri = request.getRequestURI();
//            System.out.println(uri);
//            String[] split = uri.split("/");
//            Integer examIdInUri = Integer.valueOf(split[1]);
//            if(examIdInUri.equals(examId))
//                return true;
//            else
//                return false;
//        } else {
//            return false;
//        }
        return true;
    }
}