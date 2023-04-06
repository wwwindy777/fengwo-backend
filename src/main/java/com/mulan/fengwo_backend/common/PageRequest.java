package com.mulan.fengwo_backend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用分页请求类
 * 需要用的分页的请求继承该类即可携带分页参数查询
 * @author mulan
 */
@Data
public class PageRequest implements Serializable {
    private static final long serialVersionUID = -6504628808108720782L;
    protected int pageNum;
    protected int pageSize;
}
