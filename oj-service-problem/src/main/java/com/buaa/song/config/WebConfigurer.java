package com.buaa.song.config;

import com.buaa.song.interceptor.ProblemInterceptor;
import com.buaa.song.interceptor.RequestHeaderInterceptor;
import com.buaa.song.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @FileName: WebConfigurer
 * @author: ProgrammerZhao
 * @Date: 2021/11/17
 * @Description:
 */
//@Configuration
public class WebConfigurer implements WebMvcConfigurer {

    @Autowired
    private RequestHeaderInterceptor requestHeaderInterceptor;
    @Autowired
    private ProblemInterceptor problemInterceptor;
    @Autowired
    private TokenInterceptor tokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestHeaderInterceptor).addPathPatterns("/**");
        registry.addInterceptor(problemInterceptor)
                .addPathPatterns("/problem/*/info")
                .addPathPatterns("/problem/*/submission")
                .addPathPatterns("/problem/*/submit");
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/problem/*/submission")
                .addPathPatterns("/problem/*/submit");
    }
}
