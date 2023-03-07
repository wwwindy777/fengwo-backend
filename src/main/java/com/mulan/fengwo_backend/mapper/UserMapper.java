package com.mulan.fengwo_backend.mapper;

import com.mulan.fengwo_backend.model.domain.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author wwwwind
* @description 针对表【user(伙伴匹配表)】的数据库操作Mapper
* @createDate 2022-12-31 17:27:09
* @Entity com.mulan.fengwo_backend.model.domain.User
*/
public interface UserMapper {
    List<User> getAllUsers();
    //根据标签搜索用户
    List<User> getUsersByTags(List<String> tagNameList);
    int deleteByPrimaryKey(Long id);

    int insert(User record);

    boolean insertSelective(User record);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    //登陆时调用
    User loginSearch(@Param("account") String userAccount, @Param("password") String userPassword);

    List<User> getTeamUserList(Long id);

    List<User> getUsersByCondition(User queryUser);
}
