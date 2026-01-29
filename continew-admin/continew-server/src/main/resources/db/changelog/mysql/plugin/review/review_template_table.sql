-- liquibase formatted sql

-- changeset zjx:1
-- comment 初始化流程模板管理模块表结构

-- 评审流程模板表
CREATE TABLE IF NOT EXISTS `review_process_template` (
    `id`            bigint(20)   AUTO_INCREMENT              COMMENT '模板ID',
    `template_name` varchar(100) NOT NULL                    COMMENT '模板名称',
    `template_code` varchar(50)  NOT NULL                    COMMENT '模板编码',
    `description`   varchar(500) DEFAULT NULL                COMMENT '模板描述',
    `audit_rounds`  int          NOT NULL DEFAULT 0          COMMENT '审核轮次（0-10，0表示跳过）',
    `review_rounds` int          NOT NULL DEFAULT 0          COMMENT '评审轮次（0-10，0表示跳过）',
    `decision_rounds` int        NOT NULL DEFAULT 1          COMMENT '决策轮次（1-10，至少1轮）',
    `dept_ids`      json         DEFAULT NULL                COMMENT '可见部门ID列表（JSON数组，如：[1,2,3]）',
    `role_ids`      json         DEFAULT NULL                COMMENT '可见角色ID列表（JSON数组，如：[10,20]）',
    `is_public`     bit(1)       NOT NULL DEFAULT b'0'       COMMENT '是否公开（1=公开，所有人可见；0=限制可见）',
    `status`        tinyint(1)   UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态（1：启用；2：禁用）',
    `sort`          int          NOT NULL DEFAULT 0          COMMENT '排序',
    `create_user`   bigint(20)   NOT NULL                    COMMENT '创建人',
    `create_time`   datetime     NOT NULL                    COMMENT '创建时间',
    `update_user`   bigint(20)   DEFAULT NULL                COMMENT '修改人',
    `update_time`   datetime     DEFAULT NULL                COMMENT '修改时间',
    `deleted`       bigint(20)   NOT NULL DEFAULT 0          COMMENT '是否已删除（0：否；id：是）',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_code`(`template_code`, `deleted`),
    INDEX `idx_status`(`status`, `deleted`),
    INDEX `idx_public`(`is_public`, `deleted`),
    INDEX `idx_create_user`(`create_user`),
    INDEX `idx_update_user`(`update_user`),
    INDEX `idx_deleted`(`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评审流程模板表';

-- 流程模板轮次名称表
CREATE TABLE IF NOT EXISTS `review_process_template_round_name` (
    `id`             bigint(20)  AUTO_INCREMENT        COMMENT 'ID',
    `template_id`    bigint(20)  NOT NULL              COMMENT '流程模板ID',
    `round_type`     varchar(20) NOT NULL              COMMENT '轮次类型（AUDIT=审核,REVIEW=评审,DECISION=决策）',
    `round_sequence` int         NOT NULL              COMMENT '轮次序号（第几轮，从1开始）',
    `round_name`     varchar(100) NOT NULL             COMMENT '轮次名称',
    `create_user`    bigint(20)  NOT NULL              COMMENT '创建人',
    `create_time`    datetime    NOT NULL              COMMENT '创建时间',
    `update_user`    bigint(20)  DEFAULT NULL          COMMENT '修改人',
    `update_time`    datetime    DEFAULT NULL          COMMENT '修改时间',
    `deleted`        bigint(20)  NOT NULL DEFAULT 0    COMMENT '是否已删除（0：否；id：是）',
    PRIMARY KEY (`id`),
    INDEX `idx_template`(`template_id`, `deleted`),
    INDEX `idx_sequence`(`template_id`, `round_type`, `round_sequence`, `deleted`),
    INDEX `idx_create_user`(`create_user`),
    INDEX `idx_update_user`(`update_user`),
    INDEX `idx_deleted`(`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程模板轮次名称表';

-- 管理流程模板表
CREATE TABLE IF NOT EXISTS `review_management_template` (
    `id`            bigint(20)   AUTO_INCREMENT              COMMENT '模板ID',
    `template_name` varchar(100) NOT NULL                    COMMENT '模板名称',
    `template_code` varchar(50)  NOT NULL                    COMMENT '模板编码',
    `description`   varchar(500) DEFAULT NULL                COMMENT '模板描述',
    `dept_ids`      json         DEFAULT NULL                COMMENT '可见部门ID列表',
    `role_ids`      json         DEFAULT NULL                COMMENT '可见角色ID列表',
    `is_public`     bit(1)       NOT NULL DEFAULT b'0'       COMMENT '是否公开',
    `status`        tinyint(1)   UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态（1：启用；2：禁用）',
    `sort`          int          NOT NULL DEFAULT 0          COMMENT '排序',
    `create_user`   bigint(20)   NOT NULL                    COMMENT '创建人',
    `create_time`   datetime     NOT NULL                    COMMENT '创建时间',
    `update_user`   bigint(20)   DEFAULT NULL                COMMENT '修改人',
    `update_time`   datetime     DEFAULT NULL                COMMENT '修改时间',
    `deleted`       bigint(20)   NOT NULL DEFAULT 0          COMMENT '是否已删除（0：否；id：是）',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_code`(`template_code`, `deleted`),
    INDEX `idx_status`(`status`, `deleted`),
    INDEX `idx_public`(`is_public`, `deleted`),
    INDEX `idx_create_user`(`create_user`),
    INDEX `idx_update_user`(`update_user`),
    INDEX `idx_deleted`(`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理流程模板表';

-- 管理流程阶段表
CREATE TABLE IF NOT EXISTS `review_management_stage` (
    `id`          bigint(20)   AUTO_INCREMENT        COMMENT '阶段ID',
    `template_id` bigint(20)   NOT NULL              COMMENT '所属模板ID',
    `stage_name`  varchar(100) NOT NULL              COMMENT '阶段名称',
    `stage_type`  varchar(20)  NOT NULL              COMMENT '阶段类型（KICKOFF=立项，EXECUTION=执行，ACCEPTANCE=验收）',
    `stage_order` int          NOT NULL              COMMENT '阶段顺序（从1开始）',
    `is_required` bit(1)       NOT NULL DEFAULT b'1' COMMENT '是否必须（1=必须，0=可选）',
    `create_user` bigint(20)   NOT NULL              COMMENT '创建人',
    `create_time` datetime     NOT NULL              COMMENT '创建时间',
    `update_user` bigint(20)   DEFAULT NULL          COMMENT '修改人',
    `update_time` datetime     DEFAULT NULL          COMMENT '修改时间',
    `deleted`     bigint(20)   NOT NULL DEFAULT 0    COMMENT '是否已删除（0：否；id：是）',
    PRIMARY KEY (`id`),
    INDEX `idx_template`(`template_id`, `stage_order`, `deleted`),
    INDEX `idx_create_user`(`create_user`),
    INDEX `idx_update_user`(`update_user`),
    INDEX `idx_deleted`(`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理流程阶段表';
