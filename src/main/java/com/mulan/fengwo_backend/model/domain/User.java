package com.mulan.fengwo_backend.model.domain;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 伙伴匹配表
 * @TableName user
 */
@Data
public class User implements Serializable {
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
     * 密码
     */
    private String userPassword;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户状态（0-正常）
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除（逻辑）
     */
    private Integer isDelete;

    /**
     * 用户权限
     */
    private Integer userRole;

    /**
     * 标签列表
     */
    private String tag;

    private static final long serialVersionUID = 1L;
}