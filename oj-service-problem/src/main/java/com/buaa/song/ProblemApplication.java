package com.buaa.song;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @FileName: ProblemApplication
 * @author: ProgrammerZhao
 * @Date: 2021/2/20
 * @Description:
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching
public class ProblemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProblemApplication.class,args);
    }
}