package com.mulan.fengwo_backend.model.VO;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户返回前端封装类（脱敏）
 * @TableName user
 */
@Data
public class UserVO implements Serializable {
    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 登陆账号
     */
    private String userAccount;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    /**
     * 标签列表
     */
    private String tag;

    private static final long serialVersionUID = 1L;
}