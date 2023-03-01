package com.mulan.fengwo_backend.service.impl;

import com.mulan.fengwo_backend.common.ErrorCode;
import com.mulan.fengwo_backend.exceptions.BusinessException;
import com.mulan.fengwo_backend.mapper.UserTeamMapper;
import com.mulan.fengwo_backend.model.domain.UserTeam;
import com.mulan.fengwo_backend.service.UserTeamService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserTeamServiceImpl implements UserTeamService {
    @Resource
    private UserTeamMapper userTeamMapper;

    @Override
    public List<UserTeam> getUserTeamByCondition(UserTeam userTeam) {
        List<UserTeam> userTeams = userTeamMapper.getUserTeamByCondition(userTeam);
        //正常情况应该只有一条
        if (userTeams.size() > 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "匹配信息多于一条");
        }
        return userTeams;
    }

    @Override
    public boolean addUserTeam(UserTeam userTeam) {
        return userTeamMapper.insertSelective(userTeam);
    }

    @Override
    public boolean delete(Long id) {
        return userTeamMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<UserTeam> getUserJoinTeams(Long userId) {
        return userTeamMapper.getTeamsByUserId(userId);
    }

    @Override
    public List<UserTeam> getTeamUsers(Long teamId) {
        return userTeamMapper.getTeamUsersByTeamId(teamId);
    }

    @Override
    public boolean deleteByTeamId(Long teamId) {
        return userTeamMapper.deleteByTeamId(teamId);
    }

}
