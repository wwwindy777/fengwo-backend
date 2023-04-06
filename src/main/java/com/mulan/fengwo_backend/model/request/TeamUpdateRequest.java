package com.mulan.fengwo_backend.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 更新队伍请求
 * @author mulan
 */
@Data
public class TeamUpdateRequest implements Serializable {
    private static final long serialVersionUID = 4465509363973339779L;
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
     * 过期时间
     */
    private Date expireTime;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;
}
