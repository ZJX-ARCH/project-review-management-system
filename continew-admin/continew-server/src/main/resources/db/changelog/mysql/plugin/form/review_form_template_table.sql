-- liquibase formatted sql

-- changeset zjx:form-template-table-1
-- comment 创建表单模板管理模块表结构

-- 表单模板主表
CREATE TABLE IF NOT EXISTS `review_form_template` (
    `id`            bigint(20)   AUTO_INCREMENT              COMMENT '模板ID',
    `template_name` varchar(100) NOT NULL                    COMMENT '模板名称',
    `template_code` varchar(50)  NOT NULL                    COMMENT '模板编码（唯一）',
    `template_type` tinyint(1)   UNSIGNED NOT NULL           COMMENT '模板类型（1=申请,2=审核,3=评审,4=决策,5=立项阶段,6=执行阶段,7=验收阶段）',
    `description`   varchar(500) DEFAULT NULL                COMMENT '模板描述',
    `layout_config` json         DEFAULT NULL                COMMENT '布局配置（JSON格式：gridCols、labelWidth、labelAlign）',
    `status`        tinyint(1)   UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态（1：启用；2：禁用）',
    `sort`          int          NOT NULL DEFAULT 0          COMMENT '排序',
    `create_user`   bigint(20)   NOT NULL                    COMMENT '创建人',
    `create_time`   datetime     NOT NULL                    COMMENT '创建时间',
    `update_user`   bigint(20)   DEFAULT NULL                COMMENT '修改人',
    `update_time`   datetime     DEFAULT NULL                COMMENT '修改时间',
    `deleted`       bigint(20)   NOT NULL DEFAULT 0          COMMENT '是否已删除（0：否；id：是）',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_template_code`(`template_code`, `deleted`),
    INDEX `idx_template_type`(`template_type`, `deleted`),
    INDEX `idx_status`(`status`, `deleted`),
    INDEX `idx_create_user`(`create_user`),
    INDEX `idx_update_user`(`update_user`),
    INDEX `idx_deleted`(`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表单模板主表';

-- 表单字段配置表
CREATE TABLE IF NOT EXISTS `review_form_field` (
    `id`           bigint(20)  AUTO_INCREMENT                  COMMENT '字段ID',
    `template_id`  bigint(20)  NOT NULL                        COMMENT '模板ID',
    `field_name`   varchar(100) NOT NULL                       COMMENT '字段名称',
    `field_code`   varchar(50) NOT NULL                        COMMENT '字段编码',
    `field_type`   varchar(20) NOT NULL                        COMMENT '字段类型（TEXT/TEXTAREA/NUMBER/DATE/SELECT/RADIO/CHECKBOX/FILE/FILE_TEMPLATE/TABLE/SCORE）',
    `span`         tinyint(2)  NOT NULL DEFAULT 12             COMMENT '栅格宽度（1-24）',
    `is_required`  bit(1)      NOT NULL DEFAULT b'0'           COMMENT '是否必填',
    `sort`         int         NOT NULL DEFAULT 0              COMMENT '排序（决定字段显示顺序）',
    `field_config` json        DEFAULT NULL                    COMMENT '字段特定配置（根据字段类型不同而不同）',
    `create_user`  bigint(20)  NOT NULL                        COMMENT '创建人',
    `create_time`  datetime    NOT NULL                        COMMENT '创建时间',
    `update_user`  bigint(20)  DEFAULT NULL                    COMMENT '修改人',
    `update_time`  datetime    DEFAULT NULL                    COMMENT '修改时间',
    `deleted`      bigint(20)  NOT NULL DEFAULT 0              COMMENT '是否已删除（0：否；id：是）',
    PRIMARY KEY (`id`),
    INDEX `idx_template_id`(`template_id`, `deleted`),
    UNIQUE INDEX `uk_template_field`(`template_id`, `field_code`, `deleted`),
    INDEX `idx_sort`(`template_id`, `sort`, `deleted`),
    INDEX `idx_create_user`(`create_user`),
    INDEX `idx_update_user`(`update_user`),
    INDEX `idx_deleted`(`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表单字段配置表';

-- 表单模板文件表
CREATE TABLE IF NOT EXISTS `review_form_template_file` (
    `id`          bigint(20)  AUTO_INCREMENT        COMMENT '文件关联ID',
    `template_id` bigint(20)  NOT NULL              COMMENT '模板ID',
    `field_id`    bigint(20)  DEFAULT NULL          COMMENT '字段ID（FILE_TEMPLATE类型字段关联）',
    `file_id`     bigint(20)  NOT NULL              COMMENT '文件ID（关联sys_file表）',
    `file_type`   varchar(20) DEFAULT NULL          COMMENT '文件类型（TEMPLATE=模板文件,EXAMPLE=示例文件）',
    `description` varchar(200) DEFAULT NULL         COMMENT '文件说明',
    `sort`        int         NOT NULL DEFAULT 0    COMMENT '排序',
    `create_user` bigint(20)  NOT NULL              COMMENT '创建人',
    `create_time` datetime    NOT NULL              COMMENT '创建时间',
    `deleted`     bigint(20)  NOT NULL DEFAULT 0    COMMENT '是否已删除（0：否；id：是）',
    PRIMARY KEY (`id`),
    INDEX `idx_template_id`(`template_id`, `deleted`),
    INDEX `idx_field_id`(`field_id`, `deleted`),
    INDEX `idx_file_id`(`file_id`, `deleted`),
    INDEX `idx_create_user`(`create_user`),
    INDEX `idx_deleted`(`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表单模板文件表';
