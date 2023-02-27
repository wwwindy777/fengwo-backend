package com.mulan.fengwo_backend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用分页请求类
 * 需要用的分页的请求继承该类即可携带分页参数查询
 */
@Data
public class PageRequest implements Serializable {
    protected int pageNum;
    protected int pageSize;
}
