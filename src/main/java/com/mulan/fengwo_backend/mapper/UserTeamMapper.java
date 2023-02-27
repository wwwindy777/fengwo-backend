package com.mulan.fengwo_backend.mapper;

import com.mulan.fengwo_backend.model.domain.UserTeam;

/**
* @author wwwwind
* @description 针对表【user_team(用户队伍关系)】的数据库操作Mapper
* @createDate 2023-02-27 08:51:58
* @Entity com.mulan.fengwo_backend.model.domain.UserTeam
*/
public interface UserTeamMapper {

    int deleteByPrimaryKey(Long id);

    int insert(UserTeam record);

    int insertSelective(UserTeam record);

    UserTeam selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserTeam record);

    int updateByPrimaryKey(UserTeam record);

}
