package com.mulan.fengwo_backend.service.impl;

import com.mulan.fengwo_backend.mapper.UserTeamMapper;
import com.mulan.fengwo_backend.model.domain.UserTeam;
import com.mulan.fengwo_backend.service.UserTeamService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserTeamServiceImpl implements UserTeamService {
    @Resource
    private UserTeamMapper userTeamMapper;

    @Override
    public boolean addUserTeam(UserTeam userTeam) {
        return userTeamMapper.insertSelective(userTeam);
    }

}
