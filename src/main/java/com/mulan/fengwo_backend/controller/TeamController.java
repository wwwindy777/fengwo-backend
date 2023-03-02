package com.mulan.fengwo_backend.controller;

import com.mulan.fengwo_backend.common.BaseResponse;
import com.mulan.fengwo_backend.common.DeleteRequest;
import com.mulan.fengwo_backend.common.ErrorCode;
import com.mulan.fengwo_backend.common.ResultUtils;
import com.mulan.fengwo_backend.constant.UserConstant;
import com.mulan.fengwo_backend.exceptions.BusinessException;
import com.mulan.fengwo_backend.model.VO.TeamVO;
import com.mulan.fengwo_backend.model.domain.Team;
import com.mulan.fengwo_backend.model.domain.User;
import com.mulan.fengwo_backend.model.domain.UserTeam;
import com.mulan.fengwo_backend.model.dto.TeamQuery;
import com.mulan.fengwo_backend.model.request.TeamJoinRequest;
import com.mulan.fengwo_backend.model.request.TeamQuitRequest;
import com.mulan.fengwo_backend.model.request.TeamUpdateRequest;
import com.mulan.fengwo_backend.service.TeamService;
import com.mulan.fengwo_backend.service.UserService;
import com.mulan.fengwo_backend.service.UserTeamService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/team")
@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
public class TeamController {
    @Resource
    private TeamService teamService;
    @Resource
    private UserService userService;
    @Resource
    private UserTeamService userTeamService;

    /**
     * @param team
     * @return 返回插入成功的id，要是Long类型，自己写的Integer
     */
    @Operation(summary = "添加队伍")
    @PostMapping("/add")
    //TODO:添加队伍的请求包含很多没用的参数，再封装成一个类
    public BaseResponse<Long> addTeam(@RequestBody Team team, HttpServletRequest request) {
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User addUser = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        Long res = teamService.addTeam(team, addUser);
        //根据mybatis的获取自增主键值策略，插入成功后会把自动生成的主键值赋给原对象
        return ResultUtils.success(res);
    }

    @Operation(summary = "删除队伍")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        boolean result = teamService.deleteTeam(id, request);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return ResultUtils.success(true);
    }


    @Operation(summary = "修改队伍")
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        boolean res = teamService.updateTeam(teamUpdateRequest, request);
        if (!res) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        return ResultUtils.success(true);
    }


    @Operation(summary = "查询单个队伍")
    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team res = teamService.getTeamById(id);
        return ResultUtils.success(res);
    }

    @Operation(summary = "查询符合条件队伍列表")
    @GetMapping("/list")
    public BaseResponse<List<TeamVO>> listTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isAdmin = userService.isAdmin(request);
        List<TeamVO> teams = teamService.getTeamsByCondition(teamQuery, isAdmin);
        //TODO：拿到队伍后判断当前用户是否加入了队伍，封装到返回类中，可以减少前端查询次数
        return ResultUtils.success(teams);
    }

    @Operation(summary = "加入队伍")
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = teamService.joinTeam(teamJoinRequest, request);
        return ResultUtils.success(result);
    }

    @Operation(summary = "退出队伍")
    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = teamService.quitTeam(teamQuitRequest, request);
        return ResultUtils.success(result);
    }

    @Operation(summary = "获取我创建的队伍")
    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamVO>> listMyCreateTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getCurrentUser(request);
        teamQuery.setUserId(loginUser.getId());
        List<TeamVO> teamList = teamService.getTeamsByCondition(teamQuery, true);
        return ResultUtils.success(teamList);
    }


    @Operation(summary = "获取我加入的队伍")
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamVO>> listMyJoinTeams(TeamQuery teamQuery,HttpServletRequest request) {
        User loginUser = userService.getCurrentUser(request);
        List<UserTeam> userTeamList = userTeamService.getUserJoinTeams(loginUser.getId());
        // 取出不重复的队伍 id
        // teamId userId
        // 1, 2
        // 1, 3
        // 2, 3
        // result
        // 1 => 2, 3
        // 2 => 3
        Map<Long, List<UserTeam>> listMap = userTeamList.stream()
                .collect(Collectors.groupingBy(UserTeam::getTeamId));
        List<Long> idList = new ArrayList<>(listMap.keySet());
        teamQuery.setIdList(idList);
        List<TeamVO> teamList = teamService.getTeamsByCondition(teamQuery,true);
        return ResultUtils.success(teamList);
    }
}
