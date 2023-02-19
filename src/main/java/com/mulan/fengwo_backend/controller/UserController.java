package com.mulan.fengwo_backend.controller;

import com.mulan.fengwo_backend.common.BaseResponse;
import com.mulan.fengwo_backend.common.ErrorCode;
import com.mulan.fengwo_backend.common.ResultUtils;
import com.mulan.fengwo_backend.constant.UserConstant;
import com.mulan.fengwo_backend.exceptions.BusinessException;
import com.mulan.fengwo_backend.model.domain.User;
import com.mulan.fengwo_backend.model.request.UserLoginRequest;
import com.mulan.fengwo_backend.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
public class UserController {
    @Resource
    UserServiceImpl userService;

    @Operation(summary = "用户登陆")
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        //判断登陆请求是否为空
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        //获取用户
        User user = userService.userLogin(userLoginRequest, request);
        return ResultUtils.success(user);
    }

    @Operation(summary = "获取当前用户")
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User cur = userService.getCurrentUser(request);
        return ResultUtils.success(cur);
    }

    @Operation(summary = "更新用户信息")
    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request) {
        if (user == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        //获取当前用户信息，根据请求方传来的cookie
        User currentUser = userService.getCurrentUser(request);
        int res = userService.updateUser(user, currentUser);
        //如果修改成功更新cookie
        if (res > 0) {
            User newUser = userService.searchUserById(user.getId());
            request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, newUser);
        }
        return ResultUtils.success(res);
    }

    @Operation(summary = "按标签搜索用户（匹配所有标签）")
    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUserByTags(@RequestParam List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        List<User> users = userService.searchUsersByTags(tagNameList);
        return ResultUtils.success(users);
    }

    @Operation(summary = "主页推荐用户")
    @GetMapping("/recommend")
    public BaseResponse<List<User>> recommendUsers(int pageNum,int pageSize,HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        List<User> users = userService.recommendUsers(pageNum,pageSize,request);
        //用户脱敏
        List<User> safetyUsers = users.stream().map(userService::getSafetyUser).collect(Collectors.toList());
        return ResultUtils.success(safetyUsers);
    }
}
