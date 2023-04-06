package com.mulan.fengwo_backend.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户退出队伍请求体
 * @author mulan
 */
@Data
public class TeamQuitRequest implements Serializable {

    private static final long serialVersionUID = -2515317304820333592L;
    /**
     * id
     */
    private Long teamId;

}