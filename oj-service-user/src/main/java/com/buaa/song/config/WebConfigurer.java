package com.buaa.song.config;

import com.buaa.song.interceptor.RequestHeaderInterceptor;
import com.buaa.song.interceptor.TokenInterceptor;
// import jdk.nashorn.internal.parser.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @FileName: WebConfigurer
 * @author: ProgrammerZhao
 * @Date: 2021/11/16
 * @Description:
 */
//@Configuration
public class WebConfigurer implements WebMvcConfigurer {

    @Autowired
    private RequestHeaderInterceptor requestHeaderInterceptor;
    @Autowired
    private TokenInterceptor tokenInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestHeaderInterceptor).addPathPatterns("/**");
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/user/info")
                .addPathPatterns("/user/edit")
                .addPathPatterns("/user/updatepwd");
    }
}
