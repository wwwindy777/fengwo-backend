package com.mulan.fengwo_backend.model.domain;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户队伍关系
 * @TableName user_team
 */
@Data
public class UserTeam implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 队伍id
     */
    private Long teamId;

    /**
     * 加入时间
     */
    private Date joinTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 是否删除
     * TODO：逻辑删除，MP可以通过注解表示，mybatis要自己搞
     */
    private Integer isDelete;

    private static final long serialVersionUID = 1L;
}