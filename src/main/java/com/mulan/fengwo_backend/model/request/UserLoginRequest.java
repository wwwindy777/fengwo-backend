package com.mulan.fengwo_backend.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登陆请求
 * @author mulan
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = 3735530231849206054L;
    private String userAccount;
    private String userPassword;
}
