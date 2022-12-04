package com.buaa.song.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * @FileName: RequestUtil
 * @author: ProgrammerZhao
 * @Date: 2021/10/29
 * @Description:
 */

public class RequestUtil {

    public static Integer getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        System.out.println(token);
        return (Integer) JwtUtil.decode(token).get("id");
    }
}