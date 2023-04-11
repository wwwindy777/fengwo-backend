package com.mulan.fengwo_backend.constant;

/**
 * redis key常量
 * @author mulan
 */
public interface RedisConstant {
    String JOIN_TEAM_LOCK = "fengwo:join_team:teamId:";
    String ADD_TEAM_LOCK = "fengwo:add_team:userId:";
    String RECOMMEND_USERS =  "fengwo:recommend:userId:";
}
