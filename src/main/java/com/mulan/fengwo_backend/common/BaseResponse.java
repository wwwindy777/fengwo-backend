package com.mulan.fengwo_backend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 前端通用返回包装类
 * @author mulan
 * @param <T>
 */
@Data
public class BaseResponse<T> implements Serializable {
    private static final long serialVersionUID = 1149614098852289691L;
    private int code;
    private T Data;
    private String message;
    private String description;

    public BaseResponse(Integer code, T data, String message,String description) {
        this.code = code;
        Data = data;
        this.message = message;
        this.description = description;
    }


    public BaseResponse(ErrorCode errorCode, String message,String description) {
        this(errorCode.getCode(),null,message,description);
    }

    public BaseResponse(ErrorCode errorCode,String description){
        this(errorCode.getCode(),null,errorCode.getMessage(),description);
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(),null,errorCode.getMessage(),"");
    }
}
