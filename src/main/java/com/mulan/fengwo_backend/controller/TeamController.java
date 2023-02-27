package com.mulan.fengwo_backend.controller;

import com.mulan.fengwo_backend.common.BaseResponse;
import com.mulan.fengwo_backend.common.ErrorCode;
import com.mulan.fengwo_backend.common.ResultUtils;
import com.mulan.fengwo_backend.constant.UserConstant;
import com.mulan.fengwo_backend.exceptions.BusinessException;
import com.mulan.fengwo_backend.model.domain.Team;
import com.mulan.fengwo_backend.model.domain.User;
import com.mulan.fengwo_backend.model.dto.TeamQuery;
import com.mulan.fengwo_backend.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/team")
@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
public class TeamController {
    @Resource
    private TeamService teamService;

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
    public BaseResponse<Boolean> deleteTeam(@RequestBody long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean res = teamService.deleteTeamById(id);
        if (!res) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return ResultUtils.success(true);
    }

    @Operation(summary = "修改队伍")
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody Team team) {
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        boolean res = teamService.updateTeam(team);
        if (!res) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新失败");
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
        if (res == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }
        return ResultUtils.success(res);
    }

    @Operation(summary = "查询符合条件队伍列表(含分页功能)")
    @GetMapping("/list")
    public BaseResponse<List<Team>> listTeams(TeamQuery teamQuery) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<Team> teams = teamService.getTeamsByCondition(teamQuery);
        return ResultUtils.success(teams);
    }
}
