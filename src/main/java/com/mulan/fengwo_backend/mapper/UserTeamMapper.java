package com.mulan.fengwo_backend.mapper;

import com.mulan.fengwo_backend.model.domain.UserTeam;

import java.util.List;

/**
* @author wwwwind
* @description 针对表【user_team(用户队伍关系)】的数据库操作Mapper
* @createDate 2023-02-27 08:51:58
* @Entity com.mulan.fengwo_backend.model.domain.UserTeam
*/
public interface UserTeamMapper {

    boolean deleteByPrimaryKey(Long id);

    int insert(UserTeam record);

    boolean insertSelective(UserTeam record);

    UserTeam selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserTeam record);

    int updateByPrimaryKey(UserTeam record);

    List<UserTeam> getTeamsByUserId(Long userId);

    List<UserTeam> getTeamUsersByTeamId(Long teamId);

    List<UserTeam> getUserTeamByCondition(UserTeam userTeam);

    boolean deleteByTeamId(Long teamId);
}
