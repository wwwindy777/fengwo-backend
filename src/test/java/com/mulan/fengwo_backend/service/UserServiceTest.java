package com.mulan.fengwo_backend.service;

import com.mulan.fengwo_backend.model.domain.User;
import com.mulan.fengwo_backend.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Slf4j
public class UserServiceTest {
    @Resource
    UserServiceImpl userService;

    @Test
    void testSearchUsersByTags(){
        List<String> tags = Arrays.asList("java","python","c++");
        val users = userService.searchUsersByTags(tags);
        log.info(users.toString());
        assertNotNull(users);
    }
}
