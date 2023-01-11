package com.mulan.fengwo_backend.controller;

import com.mulan.fengwo_backend.common.BaseResponse;
import com.mulan.fengwo_backend.common.ErrorCode;
import com.mulan.fengwo_backend.common.ResultUtils;
import com.mulan.fengwo_backend.exceptions.BusinessException;
import com.mulan.fengwo_backend.model.domain.User;
import com.mulan.fengwo_backend.service.impl.UserServiceImpl;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@CrossOrigin
public class UserController {
    @Resource
    UserServiceImpl userService;
    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUserByTags(@RequestParam List<String> tagNameList,HttpSession session){
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        List<User> users = userService.searchUsersByTags(tagNameList);
        //测试session redis
        if (session.getAttribute("testSession") == null){
            session.setAttribute("testSession",users.get(0));
            System.out.println("创建了session");
        }else {
            session.invalidate();
            System.out.println("移除了session");
        }
        return ResultUtils.success(users);
    }
}
