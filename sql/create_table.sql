create table user
(
    id           bigint auto_increment comment '用户id'
        primary key,
    userName     varchar(256)                       null comment '用户名',
    userAccount  varchar(256)                       not null comment '登陆账号',
    avatarUrl    varchar(256)                       null comment '头像',
    gender       tinyint  default 0                 not null comment '性别',
    userPassword varchar(256)                       not null comment '密码',
    phone        varchar(256)                       null comment '电话',
    email        varchar(256)                       null comment '邮箱',
    userStatus   int      default 0                 not null comment '用户状态（0-正常）',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除（逻辑）',
    userRole     int                                not null comment '用户权限',
    tag          varchar(1024)                      null comment '标签列表',
    constraint userAccount_index
        unique (userAccount)
)
    comment '伙伴匹配表';

create table tag
(
    id         bigint auto_increment
        primary key,
    tagName    varchar(256)                       null comment '标签名称',
    userId     bigint                             null comment '用户id',
    parentId   bigint                             null comment '父标签id',
    isParent   tinyint                            null comment '0-不是，1-父标签',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    constraint uniidx_tagName
        unique (tagName)
);

create index idx_userId
    on tag (userId);
