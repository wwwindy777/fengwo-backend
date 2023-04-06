package com.mulan.fengwo_backend.common;

/**
 * 返回结果工具类
 * @author mulan
 */
public class ResultUtils {
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(0, data, "ok","");
    }
    public static BaseResponse<?> error(ErrorCode errorCode,String message,String description){
        return new BaseResponse<>(errorCode,message,description);
    }
    public static BaseResponse<?> error(ErrorCode errorCode,String description){
        return new BaseResponse<>(errorCode,description);
    }

    public static BaseResponse<?> error(int code, String message, String description) {
        return new BaseResponse<>(code,null,message,description);
    }
}
