package com.mulan.fengwo_backend.service;


import com.mulan.fengwo_backend.model.domain.UserTeam;

import java.util.List;


public interface UserTeamService {
    List<UserTeam> getUserTeamByCondition(UserTeam userTeam);

    boolean addUserTeam(UserTeam userTeam);

    boolean delete(Long id);

    List<UserTeam> getUserJoinTeams(Long userId);

    List<UserTeam> getTeamUsers(Long teamId);

    boolean deleteByTeamId(Long teamId);
}
