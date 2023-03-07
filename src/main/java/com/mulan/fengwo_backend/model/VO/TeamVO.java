package com.mulan.fengwo_backend.model.VO;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 返回前端队伍信息封装类
 * @TableName team
 */
@Data
public class TeamVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建队伍的用户
     */
    private UserVO createUser;

    /**
     * 入队用户列表
     */
    private List<UserVO> userList;

    /**
     * 用户是否加入队伍
     */
    private boolean hasJoinTeam = false;

    private static final long serialVersionUID = 1L;
}