-- 会话表   
CREATE TABLE `im_conversation` (
  `id`                    bigint       NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `type`                  tinyint      NOT NULL DEFAULT 1 COMMENT '类型：1=单聊 2=群聊',
  `name`                  varchar(100) NOT NULL DEFAULT '' COMMENT '会话名称（单聊为空，群聊必填）',
  `avatar`                varchar(500) NOT NULL DEFAULT '' COMMENT '会话头像（单聊为空，群聊可设）',
  `owner_id`              bigint       NOT NULL DEFAULT 0 COMMENT '群主ID（单聊为0）',
  `notice`                varchar(500) NOT NULL DEFAULT '' COMMENT '群公告（单聊为空）',
  `last_message_id`       bigint       NOT NULL DEFAULT 0 COMMENT '最后一条消息ID（冗余，用于排序）',
  `last_message_content`  varchar(200) NOT NULL DEFAULT '' COMMENT '最后一条消息摘要（冗余，用于会话列表展示）',
  `last_message_time`     datetime     NULL COMMENT '最后一条消息时间（冗余，用于排序）',
  `creator`    varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater`    varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`    bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id`  bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_last_message_time` (`last_message_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IM 会话表';


-- 会话成员表

CREATE TABLE `im_conversation_member` (
  `id`                  bigint   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `conversation_id`     bigint   NOT NULL COMMENT '会话ID',
  `user_id`             bigint   NOT NULL COMMENT '成员用户ID',
  `member_role`         tinyint  NOT NULL DEFAULT 0 COMMENT '角色：0=普通成员 1=群主 2=群管理员',
  `last_read_message_id` bigint  NOT NULL DEFAULT 0 COMMENT '该成员最后阅读的消息ID（用于计算未读数）',
  `is_muted`            bit(1)   NOT NULL DEFAULT b'0' COMMENT '是否被禁言',
  `is_pinned`           bit(1)   NOT NULL DEFAULT b'0' COMMENT '是否置顶',
  `is_disturb`          bit(1)   NOT NULL DEFAULT b'0' COMMENT '是否免打扰',
  `join_time`           datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  `creator`    varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater`    varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`    bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id`  bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_conversation_user` (`conversation_id`, `user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IM 会话成员表';


-- 消息表

CREATE TABLE `im_message` (
  `id`              bigint        NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `conversation_id` bigint        NOT NULL COMMENT '所属会话ID',
  `sender_id`       bigint        NOT NULL COMMENT '发送人用户ID',
  `content_type`    tinyint       NOT NULL DEFAULT 1 COMMENT '消息类型：1=文本 2=图片 3=文件 4=系统消息',
  `content`         varchar(4000) NOT NULL DEFAULT '' COMMENT '消息内容（文本存原文；图片/文件存 URL；系统消息存描述）',
  `file_name`       varchar(255)  NOT NULL DEFAULT '' COMMENT '文件原始名称（type=3时有效）',
  `file_size`       bigint        NOT NULL DEFAULT 0 COMMENT '文件大小字节（type=3时有效）',
  `status`          tinyint       NOT NULL DEFAULT 0 COMMENT '消息状态：0=正常 1=已撤回',
  `creator`    varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater`    varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`    bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id`  bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_conversation_id` (`conversation_id`, `id`),
  KEY `idx_sender_id` (`sender_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IM 消息表';


