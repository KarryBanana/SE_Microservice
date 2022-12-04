package com.buaa.song.interceptor;

import com.buaa.song.constant.Constant;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.buaa.song.constant.Constant.*;

/**
 * @FileName: GlobalAuthorityInterceptor
 * @author: ProgrammerZhao
 * @Date: 2021/11/16
 * @Description: 全局拦截器，用于验证请求的来源，只有来自网关和服务内部请求才能通过
 */
@Component
public class RequestHeaderInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String from = request.getHeader(requestFromHeaderName);
        if(from == null){
            response.sendError(401);
            return false;
        }

        if(from.equals(requestFromGateway) || from.equals(requestFromService))
            return true;
        else{
            response.sendError(401);
            return false;
        }
    }
}
