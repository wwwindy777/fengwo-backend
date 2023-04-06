package com.mulan.fengwo_backend.once;

import com.mulan.fengwo_backend.mapper.UserMapper;
import com.mulan.fengwo_backend.model.domain.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * 给数据库插入模拟数据
 * @author mulan
 */
@Component
public class InsertUsers {
    @Resource
    private SqlSessionFactory sqlSessionFactory;
    //@Scheduled(initialDelay = 5000,fixedRate = Long.MAX_VALUE)
    public void doInsertUser(){
        //System.currentTimeMillis();
        StopWatch sw = new StopWatch();
        sw.start();
        //获得一个可以批处理的sqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        final int INSERT_NUM = 200000;
        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
            //创建User对象
            User fakeUser = new User();
            fakeUser.setUserName("fakeWind");
            fakeUser.setUserAccount(RandomStringUtils.randomAlphabetic(10));
            fakeUser.setGender(0);
            fakeUser.setUserPassword("123");
            fakeUser.setPhone("123");
            fakeUser.setEmail("123@qq.com");
            fakeUser.setUserStatus(0);
            fakeUser.setIsDelete(0);
            fakeUser.setUserRole(0);
            fakeUser.setTag("[]");
            users.add(fakeUser);
        }
        users.forEach(userMapper::insertSelective);
        sqlSession.commit();
        sqlSession.clearCache();
        sqlSession.close();
        sw.stop();
        System.out.println("用时：" + sw.getTotalTimeMillis());
    }
}
