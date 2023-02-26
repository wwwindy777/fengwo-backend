package com.mulan.fengwo_backend;

import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;

@SpringBootTest
public class RedissonTest {
    @Resource
    RedissonClient redissonClient;
    @Test
    void redissonTest(){
        ArrayList<String> strings = new ArrayList<>();
        strings.add("haha");
        System.out.println(strings.get(0));

        RList<String> testList = redissonClient.getList("testList");
        testList.add("mulanoo");
        testList.add("nihaomumu");
        String s = testList.get(0);
        System.out.println(s);
    }
}
