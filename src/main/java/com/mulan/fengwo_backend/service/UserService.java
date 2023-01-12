package com.mulan.fengwo_backend.service;

import com.mulan.fengwo_backend.model.domain.User;
import com.mulan.fengwo_backend.model.request.UserLoginRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserService {
    //操作数据库方法
    User searchUserById(Long id);

    List<User> searchUsersByTags(List<String> tagNameList);

    User getSafetyUser(User user);

    User userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);

    int updateUser(User user, User currentUser);


    //工具方法
    User getCurrentUser(HttpServletRequest request);

    boolean isAdmin(User user);

    boolean isAdmin(HttpServletRequest request);
}
