package com.mulan.fengwo_backend.service;

import com.mulan.fengwo_backend.model.domain.User;
import com.mulan.fengwo_backend.service.impl.UserServiceImpl;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class UserServiceTest {
    @Resource
    UserServiceImpl userService;

    @Test
    void testSearchUsersByTags(){
        List<String> tags = Arrays.asList("java","python");
        val users = userService.searchUsersByTags(tags);
        assertNotNull(users);
    }
}
