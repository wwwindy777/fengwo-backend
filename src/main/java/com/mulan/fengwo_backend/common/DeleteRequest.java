package com.mulan.fengwo_backend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用删除请求
 * @author mulan
 */
@Data
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = 4245512508374331883L;
    private Long id;
}