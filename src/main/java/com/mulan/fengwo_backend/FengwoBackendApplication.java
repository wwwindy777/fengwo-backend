package com.mulan.fengwo_backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan("com.mulan.fengwo_backend.mapper")
@SpringBootApplication
public class FengwoBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FengwoBackendApplication.class, args);
    }

}
