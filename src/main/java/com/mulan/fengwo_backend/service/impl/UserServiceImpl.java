package com.mulan.fengwo_backend.service.impl;

import com.mulan.fengwo_backend.mapper.UserMapper;
import com.mulan.fengwo_backend.model.domain.User;
import com.mulan.fengwo_backend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    UserMapper userMapper;

    @Override
    public User searchUserById(Long id){
        return userMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询符合所有标签的用户
     * @param tagNameList 查询的用户需要的标签
     * @return 脱敏后的用户列表
     */
    @Override
    public List<User> searchUsersByTags(List<String> tagNameList){
        if (CollectionUtils.isEmpty(tagNameList)){
            //之后优化为抛出异常
            return null;
        }
        List<User> users = userMapper.getUsersByTags(tagNameList);
        return users.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

    //用户脱敏
    @Override
    public User getSafetyUser(User user){
        if (user == null){
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUserName(user.getUserName());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl("");
        safetyUser.setGender(user.getGender());
        safetyUser.setPhone("");
        safetyUser.setEmail("");
        safetyUser.setUserStatus(0);
        safetyUser.setUserRole(user.getUserRole());
        safetyUser.setTag(user.getTag());
        return safetyUser;
    }
}
