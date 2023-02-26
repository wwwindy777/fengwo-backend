package com.mulan.fengwo_backend;
import java.util.Date;

import com.mulan.fengwo_backend.model.domain.User;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

@SpringBootTest
public class RedisTest {
    @Resource
    RedisTemplate<String,Object> redisTemplate;
    @Test
    public void redisTest(){
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set("mulan","nihao");
        valueOperations.set("mulan1",123);
        val user = new User();
        user.setId(0L);
        user.setUserName("mulan");
        user.setUserAccount("12mm");
        user.setAvatarUrl("12123");
        user.setGender(0);
        user.setUserPassword("34567");
        user.setPhone("123456");
        user.setEmail("31231");
        user.setUserStatus(0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setIsDelete(0);
        user.setUserRole(0);
        user.setTag("[java]");
        valueOperations.set("testUser",user);
        val mulan = valueOperations.get("mulan");
        System.out.println(mulan);
        System.out.println(valueOperations.get("testUser"));
    }
}
