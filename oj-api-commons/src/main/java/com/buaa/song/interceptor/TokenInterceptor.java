package com.buaa.song.interceptor;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.buaa.song.utils.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @FileName: TokenInterceptor
 * @author: ProgrammerZhao
 * @Date: 2021/11/16
 * @Description:
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String token = request.getHeader("authorization");
        if(token == null){
            response.sendError(401,"缺少token");
            return false;
        }
        try {
            JwtUtil.isVerify(token);
        } catch (TokenExpiredException e) {
            response.sendError(401,"登录过期，请重新登录");
            return false;
        } catch (JWTVerificationException e){
            response.sendError(401,"无效的Token，请重新登录");
            return false;
        }
        Object id = JwtUtil.decode(token).get("id");
        if(id == null) {
            response.sendError(401,"Token信息丢失，请重新登录");
            return false;
        }
        return true;
    }
}
