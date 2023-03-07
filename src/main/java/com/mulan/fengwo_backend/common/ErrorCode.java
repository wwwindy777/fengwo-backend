package com.mulan.fengwo_backend.common;

public enum ErrorCode {
    SUCCESS(0,"ok"),
    PARAMS_ERROR(40000,"请求参数错误"),
    NULL_ERROR(40001,"参数为空"),
    NOT_LOGIN(40100,"未登陆"),
    NO_AUTH(40101,"无权限"),
    FORBIDDEN(40301, "禁止操作"),
    SYSTEM_ERROR(50000,"系统内部异常")
    ;
    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
