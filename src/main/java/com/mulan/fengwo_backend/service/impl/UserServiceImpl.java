package com.mulan.fengwo_backend.service.impl;

import com.mulan.fengwo_backend.mapper.UserMapper;
import com.mulan.fengwo_backend.model.domain.User;
import com.mulan.fengwo_backend.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    UserMapper userMapper;

    @Override
    public User searchUserById(Long id){
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<User> searchUsersByTags(List<String> tagNameList){
        List<User> users = userMapper.getAllUsers();
        return users;
    }
}
