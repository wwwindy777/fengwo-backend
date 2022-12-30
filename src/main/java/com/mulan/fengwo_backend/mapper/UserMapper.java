package com.mulan.fengwo_backend.mapper;

import com.mulan.fengwo_backend.model.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author wwwwind
* @description 针对表【user(伙伴匹配表)】的数据库操作Mapper
* @createDate 2022-12-31 17:27:09
* @Entity com.mulan.fengwo_backend.model.domain.User
*/
public interface UserMapper {
    List<User> getAllUsers();

    int deleteByPrimaryKey(Long id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

}
