package com.buaa.song;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @FileName: SubmissionApplication
 * @author: ProgrammerZhao
 * @Date: 2021/5/21
 * @Description:
 */
@SpringBootApplication
@EnableDiscoveryClient
public class SubmissionApplication {
    public static void main(String[] args) {
        SpringApplication.run(SubmissionApplication.class,args);
    }
}