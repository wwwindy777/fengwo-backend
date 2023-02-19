package com.mulan.fengwo_backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.mulan.fengwo_backend.mapper")
@SpringBootApplication
@EnableScheduling
public class FengwoBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FengwoBackendApplication.class, args);
    }

}
