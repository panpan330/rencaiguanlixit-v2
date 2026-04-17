package com.rehab.managerv2;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.rehab.managerv2.mapper")
@EnableScheduling
public class RehabManagerV2Application {

    public static void main(String[] args) {
        SpringApplication.run(RehabManagerV2Application.class, args);
    }

}
