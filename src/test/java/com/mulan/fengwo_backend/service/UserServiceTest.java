package com.mulan.fengwo_backend.service;

import com.mulan.fengwo_backend.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class UserServiceTest {
    @Resource
    UserServiceImpl userService;

    @Test
    void testSearchUsersByTags(){
    }
}
