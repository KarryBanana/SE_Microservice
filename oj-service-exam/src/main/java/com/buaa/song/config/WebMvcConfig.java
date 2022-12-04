package com.buaa.song.config;

import com.buaa.song.interceptor.ExamInterceptor;
import com.buaa.song.interceptor.LoginInterceptor;
import com.buaa.song.interceptor.RequestHeaderInterceptor;
import com.buaa.song.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @FileName: WebMvcConfig
 * @author: ProgrammerZhao
 * @Date: 2021/4/28
 * @Description:
 */
//@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    @Autowired
    private RequestHeaderInterceptor requestHeaderInterceptor;
    @Autowired
    private ExamInterceptor examInterceptor;

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestHeaderInterceptor)
                .addPathPatterns("/**");
        registry.addInterceptor(examInterceptor)
                .addPathPatterns("/contest/*/*");
    }
}
