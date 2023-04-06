package com.mulan.fengwo_backend.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求
 * @author mulan
 */
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = -5065060348645859195L;
    private String userAccount;

    private String userPassword;

    private String checkPassword;

}
