package com.mulan.fengwo_backend.service;

import com.mulan.fengwo_backend.model.VO.TeamVO;
import com.mulan.fengwo_backend.model.domain.Team;
import com.mulan.fengwo_backend.model.domain.User;
import com.mulan.fengwo_backend.model.dto.TeamQuery;
import com.mulan.fengwo_backend.model.request.TeamJoinRequest;
import com.mulan.fengwo_backend.model.request.TeamQuitRequest;
import com.mulan.fengwo_backend.model.request.TeamUpdateRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface TeamService {
    Long addTeam(Team team, User addUser);

    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, HttpServletRequest request);

    Team getTeamById(Long teamId);

    List<TeamVO> getTeamsByCondition(TeamQuery teamQuery, boolean isAdmin);

    boolean joinTeam(TeamJoinRequest teamJoinRequest, HttpServletRequest request);

    @Transactional(rollbackFor = Exception.class)
    boolean quitTeam(TeamQuitRequest teamQuitRequest, HttpServletRequest loginUser);

    boolean deleteTeam(long id, HttpServletRequest request);
}
