package com.mulan.fengwo_backend.service;

import com.mulan.fengwo_backend.model.VO.TeamVO;
import com.mulan.fengwo_backend.model.domain.Team;
import com.mulan.fengwo_backend.model.domain.User;
import com.mulan.fengwo_backend.model.dto.TeamQuery;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface TeamService {
    Long addTeam(Team team, User addUser);

    boolean deleteTeamById(long id);

    boolean updateTeam(Team team);

    Team getTeamById(long id);

    List<TeamVO> getTeamsByCondition(TeamQuery teamQuery, HttpServletRequest request);

}
