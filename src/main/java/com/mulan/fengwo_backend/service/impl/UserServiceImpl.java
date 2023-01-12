package com.mulan.fengwo_backend.service.impl;

import com.mulan.fengwo_backend.common.ErrorCode;
import com.mulan.fengwo_backend.constant.UserConstant;
import com.mulan.fengwo_backend.exceptions.BusinessException;
import com.mulan.fengwo_backend.mapper.UserMapper;
import com.mulan.fengwo_backend.model.domain.User;
import com.mulan.fengwo_backend.model.request.UserLoginRequest;
import com.mulan.fengwo_backend.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    UserMapper userMapper;

    @Override
    public User searchUserById(Long id) {
        return userMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询符合所有标签的用户
     *
     * @param tagNameList 标签列表
     * @return 脱敏后的用户列表
     */
    @Override
    public List<User> searchUsersByTags(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "标签为空");
        }
        List<User> users = userMapper.getUsersByTags(tagNameList);
        return users.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 用户脱敏
     * @param user
     * @return
     */
    @Override
    public User getSafetyUser(User user) {
        if (user == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUserName(user.getUserName());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setPhone("");
        safetyUser.setEmail("");
        safetyUser.setUserStatus(0);
        safetyUser.setUserRole(user.getUserRole());
        safetyUser.setCreateTime(user.getCreateTime());
        safetyUser.setTag(user.getTag());
        return safetyUser;
    }

    /**
     * 用户登陆
     *
     * @param userLoginRequest 账号密码
     * @param request          用于设置登陆态
     * @return 脱敏后的用户数据
     */
    @Override
    public User userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        //判断用户名和密码是否为空
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //todo 判断是否存在特殊字符
        //todo 密码加密
        //查询用户是否存在
        User user = userMapper.loginSearch(userAccount, userPassword);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //保存用户登陆态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
        //用户脱敏
        return getSafetyUser(user);
    }


    /**
     * 修改用户信息
     *
     * @param user
     * @param currentUser
     * @return
     */
    @Override
    public int updateUser(User user, User currentUser) {
        Long id = user.getId();
        if (id < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //判断是否有权限修改
        //1.只可以修改自己的信息
        //2.管理员可以修改任何人信息
        if (!Objects.equals(id, currentUser.getId()) &&
                !isAdmin(currentUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return userMapper.updateByPrimaryKeySelective(user);
    }

    /**
     * 获取当前用户
     *
     * @param request
     * @return
     */
    @Override
    public User getCurrentUser(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return getSafetyUser(user);
    }

    /**
     * 是否为管理员（根据用户）
     *
     * @param user
     * @return
     */
    @Override
    public boolean isAdmin(User user) {
        return user.getUserRole().equals(UserConstant.ADMIN_ROLE);
    }

    /**
     * 是否为管理员（根据cookie）
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        return user != null && Objects.equals(user.getUserRole(), UserConstant.ADMIN_ROLE);
    }
}
