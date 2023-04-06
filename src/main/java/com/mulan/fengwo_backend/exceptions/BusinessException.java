package com.mulan.fengwo_backend.exceptions;

import com.mulan.fengwo_backend.common.ErrorCode;

/**
 * 自定义异常
 * @author mulan
 */
public class BusinessException extends RuntimeException{
    private static final long serialVersionUID = 2013098325598919790L;
    private final int code;
    private final String description;

    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = "";
    }

    public BusinessException(ErrorCode errorCode,String description){
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }
}
