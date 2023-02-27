package com.mulan.fengwo_backend.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mulan.fengwo_backend.mapper.TeamMapper;
import com.mulan.fengwo_backend.model.domain.Team;
import com.mulan.fengwo_backend.model.dto.TeamQuery;
import com.mulan.fengwo_backend.service.TeamService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TeamServiceImpl implements TeamService {
    @Resource
    private TeamMapper teamMapper;

    @Override
    public boolean addTeam(Team team) {
        return teamMapper.insertSelective(team);
    }

    @Override
    public boolean deleteTeamById(long id) {
        return teamMapper.deleteByPrimaryKey(id);
    }

    @Override
    public boolean updateTeam(Team team) {
        return teamMapper.updateByPrimaryKeySelective(team);
    }

    @Override
    public Team getTeamById(long id) {
        return teamMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Team> getTeamsByCondition(TeamQuery teamQuery) {
        //简便方法：将查询条件封装进一个team对象
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery, team);
        //有分页条件时返回分页数据
        if (teamQuery.getPageNum() > 0 && teamQuery.getPageSize() > 0) {
            PageHelper.startPage(teamQuery.getPageNum(), teamQuery.getPageSize());
            //紧跟着PageHelper.startPage(pageNum,pageSize)的sql语句才被pageHelper起作用
            List<Team> teams = teamMapper.selectByCondition(team);
            PageInfo<Team> pageInfo = new PageInfo<>(teams);
            return pageInfo.getList();
        }
        return teamMapper.selectByCondition(team);
    }
}
