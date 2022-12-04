package com.buaa.song.config;

import com.buaa.song.interceptor.ClassAuthorityInterceptor;
import com.buaa.song.interceptor.RequestHeaderInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @FileName: WebConfig
 * @author: ProgrammerZhao
 * @Date: 2021/11/17
 * @Description:
 */
//@Configuration
public class WebConfigurer implements WebMvcConfigurer {
    @Autowired
    private RequestHeaderInterceptor requestHeaderInterceptor;
    @Autowired
    private ClassAuthorityInterceptor classAuthorityInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestHeaderInterceptor).addPathPatterns("/**");
        registry.addInterceptor(classAuthorityInterceptor).addPathPatterns("/class/**");
    }
}
