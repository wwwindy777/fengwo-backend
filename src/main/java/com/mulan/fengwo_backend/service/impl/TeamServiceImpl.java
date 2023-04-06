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
import com.mulan.fengwo_backend.model.request.TeamAddRequest;
import com.mulan.fengwo_backend.model.request.TeamJoinRequest;
import com.mulan.fengwo_backend.model.request.TeamQuitRequest;
import com.mulan.fengwo_backend.model.request.TeamUpdateRequest;
import com.mulan.fengwo_backend.service.TeamService;
import com.mulan.fengwo_backend.service.UserService;
import com.mulan.fengwo_backend.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class TeamServiceImpl implements TeamService {
    @Resource
    private TeamMapper teamMapper;
    @Resource
    private UserTeamService userTeamService;
    @Resource
    private UserService userService;
    @Resource
    private RedissonClient redissonClient;

    /**
     * 添加队伍
     *
     * @param team
     * @param addUser
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long addTeam(TeamAddRequest team, User addUser) {
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
        if (team.getExpireTime() != null && new Date().after(team.getExpireTime())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "已超时");
        }
        //   7. 校验用户最多创建 5 个队：此处如果用户同一时间点击多次可能创建多个队伍。TODO：加锁
        int size = teamMapper.getCreateTeamsByUserId(addUser.getId()).size();
        if (size >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多创建5个队伍");
        }
        //4. 插入队伍信息到队伍表，此处要使用事务
        Team newTeam = new Team();
        BeanUtils.copyProperties(team, newTeam);
        newTeam.setUserId(addUser.getId());
        boolean resAddTeam = teamMapper.insertSelective(newTeam);
        Long newTeamId = newTeam.getId();
        if (!resAddTeam || newTeamId == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "添加队伍失败");
        }
        //5. 插入用户  => 队伍关系到关系表，此处会嵌套其他的service，尽量不要直接使用Mapper
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(addUser.getId());
        userTeam.setTeamId(newTeamId);
        userTeam.setJoinTime(new Date());
        boolean resAddUserTeam = userTeamService.addUserTeam(userTeam);
        if (!resAddUserTeam) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "添加用户队伍关系失败");
        }
        return newTeamId;
    }

    /**
     * 更新队伍
     *
     * @param teamUpdateRequest
     * @param request
     * @return
     */
    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        User loginUser = userService.getCurrentUser(request);
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //判断队伍是否存在(获取方法中已判断)
        Team oldTeam = this.getTeamById(id);
        // 只有管理员或者队伍的创建者可以修改
        if (!Objects.equals(oldTeam.getUserId(), loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        //如果队伍状态改为加密，必须要有密码
        TeamStatusEnum statusEnum = TeamStatusEnum.getStatusByNum(teamUpdateRequest.getStatus());
        if (statusEnum.equals(TeamStatusEnum.LOCKED)) {
            if (StringUtils.isBlank(teamUpdateRequest.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密房间必须要设置密码");
            }
        }
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest, updateTeam);
        return teamMapper.updateByPrimaryKeySelective(updateTeam);
    }

    /**
     * 根据id查询队伍
     *
     * @param teamId
     * @return
     */
    @Override
    public Team getTeamById(Long teamId) {
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team res = teamMapper.selectByPrimaryKey(teamId);
        if (res == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        return res;
    }


    /**
     * 根据条件查询
     * 功能非常多，很多复用的逻辑
     *
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    @Override
    public List<TeamVO> getTeamsByCondition(TeamQuery teamQuery, boolean isAdmin, Optional<Long> userId) {
        //1. 从请求参数中取出队伍名称等查询条件，如果存在则作为查询条件（在Mapper中实现）
        List<TeamVO> resTeams = new ArrayList<>();
        //pageSize=0会查询全部结果，相当于不分页，注意关闭默认count功能
        PageHelper.startPage(teamQuery.getPageNum(), teamQuery.getPageSize());
        //紧跟着PageHelper.startPage(pageNum,pageSize)的sql语句才被pageHelper起作用
        List<Team> teams = teamMapper.selectByCondition(teamQuery);
        PageInfo<Team> pageInfo = new PageInfo<>(teams);
        teams = pageInfo.getList();
        if (teams.size() == 0) {
            return null;
        }
        for (Team team : teams) {
            TeamVO teamVO = new TeamVO();
            BeanUtils.copyProperties(team, teamVO);
            Long teamId = teamVO.getId();
            //2. 不展示已过期的队伍（根据过期时间筛选）
            Date expireTime = teamVO.getExpireTime();
            if (expireTime != null && new Date().after(expireTime)) {
                continue;
            }
            //4. 只有管理员才能查看加密还有非公开的房间
            if (!isAdmin && teamVO.getStatus() == 1) {
                continue;
            }
            //5. 查询已创建队伍的用户信息
            User createUser = userService.searchUserById(teamVO.getUserId());
            if (createUser != null) {
                UserVO createUserVO = new UserVO();
                BeanUtils.copyProperties(userService.getSafetyUser(createUser), createUserVO);
                teamVO.setCreateUser(createUserVO);
            }
            //6. 关联查询已加入队伍的用户信息
            List<UserVO> userList = userService.getTeamUserList(teamId);
            teamVO.setUserList(userList);
            //7. 是否已经加入队伍（减少前端查询次数）
            if (teamVO.getUserId() != null || userId.isPresent()) {
                UserTeam queryUserTeam = new UserTeam();
                queryUserTeam.setTeamId(teamId);
                queryUserTeam.setUserId(userId.orElse(teamVO.getUserId()));
                List<UserTeam> hasJoinTeam = userTeamService.getUserTeamByCondition(queryUserTeam);
                if (hasJoinTeam.size() == 1) {
                    teamVO.setHasJoinTeam(true);
                }
            }
            resTeams.add(teamVO);
        }
        return resTeams;
    }

    /**
     * 条件查询重载，减少传参
     *
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    @Override
    public List<TeamVO> getTeamsByCondition(TeamQuery teamQuery, boolean isAdmin) {
        return getTeamsByCondition(teamQuery, isAdmin, Optional.empty());
    }

    /**
     * 加入队伍
     *
     * @param teamJoinRequest
     * @param request
     * @return
     */
    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        User loginUser = userService.getCurrentUser(request);
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamJoinRequest.getTeamId();
        Team team = this.getTeamById(teamId);
        Date expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
        }
        Integer status = team.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getStatusByNum(status);
        if (TeamStatusEnum.PRIVATE.equals(teamStatusEnum)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "禁止加入私有队伍");
        }
        String password = teamJoinRequest.getPassword();
        if (TeamStatusEnum.LOCKED.equals(teamStatusEnum)) {
            if (StringUtils.isBlank(password) || !password.equals(team.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
            }
        }
        // 该用户已加入的队伍数量
        long userId = loginUser.getId();
        // 只有一个线程能获取到锁
        //TODO：这里加的锁比较大，只要想加入队伍都要抢锁，可以改成根据队伍名加锁，将锁名动态改变（锁名可以设一个常量）
        RLock lock = redissonClient.getLock("fengwo:join_team");
        try {
            // 抢到锁并执行
            while (true) {
                if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                    System.out.println("getLock: " + Thread.currentThread().getId());
                    List<UserTeam> hasJoinTeams = userTeamService.getUserJoinTeams(userId);
                    int hasJoinTeamNums = hasJoinTeams.size();
                    if (hasJoinTeamNums >= 5) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多创建和加入5个队伍");
                    }
                    // 不能重复加入已加入的队伍
                    for (UserTeam hasJoinTeam : hasJoinTeams) {
                        if (teamId.equals(hasJoinTeam.getTeamId())) {
                            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户已加入该队伍");
                        }
                    }
                    // 已加入队伍的人数
                    //目前查询的是用户表，改成查询user_team表效率会高点
                    long teamHasJoinNum = userService.getTeamUserList(teamId).size();
                    if (teamHasJoinNum >= team.getMaxNum()) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已满");
                    }
                    // 修改队伍信息
                    UserTeam userTeam = new UserTeam();
                    userTeam.setUserId(userId);
                    userTeam.setTeamId(teamId);
                    userTeam.setJoinTime(new Date());
                    return userTeamService.addUserTeam(userTeam);
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error", e);
            return false;
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }

    /**
     * 退出队伍
     *
     * @param teamQuitRequest
     * @param request
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
        User loginUser = userService.getCurrentUser(request);
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamQuitRequest.getTeamId();
        Team team = getTeamById(teamId);
        long userId = loginUser.getId();
        //是否加入队伍
        UserTeam queryUserTeam = new UserTeam();
        queryUserTeam.setTeamId(teamId);
        queryUserTeam.setUserId(userId);
        List<UserTeam> userTeams = userTeamService.getUserTeamByCondition(queryUserTeam);
        if (userTeams.size() == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未加入队伍");
        }
        long teamHasJoinNum = this.countTeamUserByTeamId(teamId);
        // 队伍只剩一人，解散
        if (teamHasJoinNum == 1) {
            // 删除队伍
            teamMapper.deleteByPrimaryKey(teamId);
        } else {
            // 队伍还剩至少两人
            // 是队长
            if (team.getUserId() == userId) {
                // 把队伍转移给最早加入的用户
                // 1. 查询已加入队伍的所有用户和加入时间
                List<UserTeam> userTeamList = userTeamService.getTeamUsers(teamId);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam nextUserTeam = userTeamList.get(1);
                Long nextTeamLeaderId = nextUserTeam.getUserId();
                // 更新当前队伍的队长
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextTeamLeaderId);
                boolean result = teamMapper.updateByPrimaryKeySelective(updateTeam);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队伍队长失败");
                }
            }
        }
        // 移除关系
        return userTeamService.delete(userTeams.get(0).getId());
    }

    /**
     * 删除队伍
     *
     * @param id
     * @param request
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(long id, HttpServletRequest request) {
        User loginUser = userService.getCurrentUser(request);
        // 校验队伍是否存在
        Team team = getTeamById(id);
        long teamId = team.getId();
        // 校验你是不是队伍的队长
        if (!Objects.equals(team.getUserId(), loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无删除权限");
        }
        // 移除所有加入队伍的关联信息
        boolean result = userTeamService.deleteByTeamId(teamId);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除队伍关联信息失败");
        }
        // 删除队伍
        return teamMapper.deleteByPrimaryKey(id);
    }


    /**
     * 获取某队伍当前人数
     *
     * @param teamId
     * @return
     */
    private long countTeamUserByTeamId(long teamId) {
        List<UserTeam> teamUsers = userTeamService.getTeamUsers(teamId);
        return teamUsers.size();
    }
}
