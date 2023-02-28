package com.mulan.fengwo_backend.mapper;

import com.mulan.fengwo_backend.model.domain.Team;

import java.util.List;

/**
* @author wwwwind
* @description 针对表【team(队伍)】的数据库操作Mapper
* @createDate 2023-02-27 08:48:34
* @Entity com.mulan.fengwo_backend.model.domain.Team
*/
public interface TeamMapper {

    boolean deleteByPrimaryKey(Long id);

    long insert(Team record);

    boolean insertSelective(Team record);

    Team selectByPrimaryKey(Long id);

    boolean updateByPrimaryKeySelective(Team record);

    int updateByPrimaryKey(Team record);

    List<Team> selectByCondition(Team team);

}
