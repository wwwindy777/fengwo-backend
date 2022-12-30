package com.mulan.fengwo_backend.model.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Tag implements Serializable {

    private Long id;
    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 父标签id
     */
    private Long parentId;

    /**
     * 0-不是，1-父标签
     */
    private Integer isParent;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    private static final long serialVersionUID = 1L;
}
