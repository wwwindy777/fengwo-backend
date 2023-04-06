package com.mulan.fengwo_backend.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mulan.fengwo_backend.common.ErrorCode;
import com.mulan.fengwo_backend.constant.UserConstant;
import com.mulan.fengwo_backend.exceptions.BusinessException;
import com.mulan.fengwo_backend.mapper.UserMapper;
import com.mulan.fengwo_backend.model.VO.UserVO;
import com.mulan.fengwo_backend.model.domain.User;
import com.mulan.fengwo_backend.model.request.UserLoginRequest;
import com.mulan.fengwo_backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.mulan.fengwo_backend.constant.UserConstant.SALT;

@Service
@Slf4j
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
     *
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
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        //判断是否存在特殊字符
        String validPattern = "[\\p{Punct}\\p{Space}\\p{Cntrl}]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //查询用户是否存在
        User user = userMapper.loginSearch(userAccount, encryptPassword);
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "登陆失败");
        }
        User safetyUser = getSafetyUser(user);
        //保存用户登陆态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, safetyUser);
        //用户脱敏
        return safetyUser;
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

    /**
     * 主页推荐用户列表
     *
     * @param pageNum
     * @param pageSize
     * @param request
     * @return
     */
    @Override
    public List<User> recommendUsers(int pageNum, int pageSize, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        //如果未登陆，按默认推荐
        if (user == null) {
            PageHelper.startPage(pageNum, pageSize);
            //紧跟着PageHelper.startPage(pageNum,pageSize)的sql语句才被pageHelper起作用
            List<User> users = userMapper.getAllUsers();
            PageInfo<User> pageInfo = new PageInfo<>(users);
            return pageInfo.getList();
        }
        //如果登陆按用户标签推荐
        //1.获取当前用户的标签
        String userTagString = user.getTag();
        Gson gson = new Gson();
        java.lang.reflect.Type userListType = new TypeToken<List<String>>() {
        }.getType();
        List<String> userTagList = gson.fromJson(userTagString, userListType);
        //2.搜索包含其中某个标签的用户（todo 现在还是包含所有标签的用户）
        return userMapper.getUsersByTags(userTagList);
    }

    /**
     * 根据队伍id查询队伍中的成员
     *
     * @param id
     * @return
     */
    @Override
    public List<UserVO> getTeamUserList(Long id) {
        List<User> teamUserList = userMapper.getTeamUserList(id);
        List<UserVO> teamUserVOList = new ArrayList<>();
        for (User user : teamUserList) {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(this.getSafetyUser(user), userVO);
            teamUserVOList.add(userVO);
        }
        return teamUserVOList;
    }

    /**
     * 用户注册
     *
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */
    @Override
    public Long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册信息错误");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码过短");
        }
        // 账户不能包含特殊字符
        String validPattern = "[\\p{Punct}\\p{Space}\\p{Cntrl}]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号包含特殊字符");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "较验密码不一致");
        }
        // 账户不能重复查询
        User queryUser = new User();
        queryUser.setUserAccount(userAccount);
        List<User> users = userMapper.getUsersByCondition(queryUser);
        if (users.size() > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean saveResult = userMapper.insertSelective(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败");
        }
        return user.getId();
    }
}
