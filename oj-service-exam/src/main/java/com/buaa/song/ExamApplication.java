package com.buaa.song;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @FileName: ExamApplication
 * @author: ProgrammerZhao
 * @Date: 2021/4/7
 * @Description:
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ExamApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExamApplication.class,args);
    }
}