package com.mulan.fengwo_backend.service;

import com.mulan.fengwo_backend.model.domain.Team;
import com.mulan.fengwo_backend.model.dto.TeamQuery;

import java.util.List;

public interface TeamService {
    boolean addTeam(Team team);

    boolean deleteTeamById(long id);

    boolean updateTeam(Team team);

    Team getTeamById(long id);

    List<Team> getTeamsByCondition(TeamQuery teamQuery);

}
