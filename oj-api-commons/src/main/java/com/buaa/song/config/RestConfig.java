package com.buaa.song.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @FileName: RestConfig
 * @author: ProgrammerZhao
 * @Date: 2021/3/10
 * @Description:
 */
@Configuration
public class RestConfig {

    @Bean
    // 2022-6-18 ckr尝试手动负载均衡
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}