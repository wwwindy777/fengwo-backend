package com.mulan.fengwo_backend.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mulan.fengwo_backend.common.ErrorCode;
import com.mulan.fengwo_backend.exceptions.BusinessException;
import com.mulan.fengwo_backend.mapper.TeamMapper;
import com.mulan.fengwo_backend.model.Enums.TeamStatusEnum;
import com.mulan.fengwo_backend.model.VO.TeamVO;
import com.mulan.fengwo_backend.model.VO.UserVO;
import com.mulan.fengwo_backend.model.domain.Team;
import com.mulan.fengwo_backend.model.domain.User;
import com.mulan.fengwo_backend.model.domain.UserTeam;
import com.mulan.fengwo_backend.model.dto.TeamQuery;
import com.mulan.fengwo_backend.service.TeamService;
import com.mulan.fengwo_backend.service.UserService;
import com.mulan.fengwo_backend.service.UserTeamService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TeamServiceImpl implements TeamService {
    @Resource
    private TeamMapper teamMapper;
    @Resource
    private UserTeamService userTeamService;
    @Resource
    private UserService userService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long addTeam(Team team, User addUser) {
        //1. 请求参数是否为空？
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "请求参数为空");
        }
        //2. 是否登录，未登录不允许创建
        if (addUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        //3. 校验信息
        //   1. 队伍人数 > 1 且 <= 20
        if (team.getMaxNum() == null || team.getMaxNum() <= 1 || team.getMaxNum() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数错误");
        }
        //   2. 队伍标题 <= 20
        if (StringUtils.isBlank(team.getName()) || team.getName().length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍名错误");
        }
        //   3. 描述 <= 512，描述存在时才较验
        if (StringUtils.isNotBlank(team.getDescription()) && team.getDescription().length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "描述错误");
        }
        //   4. status 是否公开（int）不传默认为 0（公开），状态设置为枚举
        int statusNum = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getStatusByNum(statusNum);
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态错误");
        }
        //   5. 如果 status 是加密状态，一定要有密码，且密码 <= 32
        String password = team.getPassword();
        if (statusEnum.equals(TeamStatusEnum.LOCKED)) {
            if (StringUtils.isBlank(password) && password.length() > 32) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码设置错误");
            }
        }
        //   6. 超时时间 > 当前时间
        if (new Date().after(team.getExpireTime())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "已超时");
        }
        //   7. 校验用户最多创建 5 个队：此处如果用户同一时间点击多次可能创建多个队伍。TODO：加锁
        Team queryTeam = new Team();
        queryTeam.setUserId(addUser.getId());
        int size = teamMapper.selectByCondition(queryTeam).size();
        if (size >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多创建5个队伍");
        }
        //4. 插入队伍信息到队伍表，此处要使用事务
        team.setId(null);
        team.setUserId(addUser.getId());
        boolean resAddTeam = teamMapper.insertSelective(team);
        Long teamId = team.getId();
        if (!resAddTeam || teamId == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "添加队伍失败");
        }
        //5. 插入用户  => 队伍关系到关系表，此处会嵌套其他的service，尽量不要直接使用Mapper
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(addUser.getId());
        userTeam.setTeamId(team.getId());
        userTeam.setJoinTime(new Date());
        boolean resAddUserTeam = userTeamService.addUserTeam(userTeam);
        if (!resAddUserTeam) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "添加用户队伍关系失败");
        }
        return teamId;
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
    public List<TeamVO> getTeamsByCondition(TeamQuery teamQuery, HttpServletRequest request) {
        //1. 从请求参数中取出队伍名称等查询条件，如果存在则作为查询条件（在Mapper中实现）
        //简便方法：将查询条件封装进一个team对象（类似MP的queryMapper）
        Team queryTeam = new Team();
        BeanUtils.copyProperties(teamQuery, queryTeam);
        List<Team> teams;
        List<TeamVO> resTeams = new ArrayList<>();
        //有分页条件时返回分页数据
        if (teamQuery.getPageNum() > 0 && teamQuery.getPageSize() > 0) {
            PageHelper.startPage(teamQuery.getPageNum(), teamQuery.getPageSize());
            //紧跟着PageHelper.startPage(pageNum,pageSize)的sql语句才被pageHelper起作用
            teams = teamMapper.selectByCondition(queryTeam);
            PageInfo<Team> pageInfo = new PageInfo<>(teams);
            teams = pageInfo.getList();
        } else {
            teams = teamMapper.selectByCondition(queryTeam);
        }
        if (teams.size() == 0) {
            return null;
        }
        for (Team team : teams) {
            TeamVO teamVO = new TeamVO();
            BeanUtils.copyProperties(team, teamVO);
            //2. 不展示已过期的队伍（根据过期时间筛选）
            Date expireTime = teamVO.getExpireTime();
            if (expireTime != null && new Date().after(expireTime)) {
                continue;
            }
            //4. **只有管理员才能查看加密还有非公开的房间**
            if (!userService.isAdmin(request) && teamVO.getStatus() == 1) {
                continue;
            }
            //5. 查询已创建队伍的用户信息
            User createUser = userService.searchUserById(teamVO.getUserId());
            UserVO createUserVO = new UserVO();
            BeanUtils.copyProperties(userService.getSafetyUser(createUser), createUserVO);
            teamVO.setCreateUser(createUserVO);
            //6. **关联查询已加入队伍的用户信息（可能会很耗费性能，建议大家用自己写 SQL 的方式实现）**
            List<UserVO> userList = userService.getTeamUserList(teamVO.getId());
            teamVO.setUserList(userList);
            resTeams.add(teamVO);
        }
        return resTeams;
    }
}
