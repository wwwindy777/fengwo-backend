package com.mulan.fengwo_backend.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户加入队伍请求
 * @author mulan
 */
@Data
public class TeamJoinRequest implements Serializable {
    private static final long serialVersionUID = 3818945541641192748L;
    /**
     * id
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;
}
