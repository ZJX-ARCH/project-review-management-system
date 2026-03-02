-- liquibase formatted sql

-- changeset zjx:review-type-table-1
-- comment 创建项目类型主表

CREATE TABLE IF NOT EXISTS `review_project_type` (
    `id`          bigint(20)   AUTO_INCREMENT              COMMENT '类型ID',
    `type_name`   varchar(100) NOT NULL                    COMMENT '类型名称',
    `type_code`   varchar(50)  NOT NULL                    COMMENT '类型编码（唯一，创建后不可修改）',
    `description` varchar(500) DEFAULT NULL                COMMENT '描述',
    `sort_order`  int          NOT NULL DEFAULT 0          COMMENT '排序',
    `status`      tinyint(1)   UNSIGNED NOT NULL DEFAULT 0 COMMENT '状态（0：草稿；1：已启用；2：已禁用）',
    `dept_id`     bigint(20)   NOT NULL DEFAULT 1          COMMENT '所属部门ID（TYPE_ADMIN数据权限范围）',
    `create_user` bigint(20)   NOT NULL                    COMMENT '创建人',
    `create_time` datetime     NOT NULL                    COMMENT '创建时间',
    `update_user` bigint(20)   DEFAULT NULL                COMMENT '修改人',
    `update_time` datetime     DEFAULT NULL                COMMENT '修改时间',
    `deleted`     bigint(20)   NOT NULL DEFAULT 0          COMMENT '是否已删除（0：否；id：是）',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_type_code`(`type_code`, `deleted`),
    INDEX `idx_status`(`status`, `deleted`),
    INDEX `idx_dept_id`(`dept_id`, `deleted`),
    INDEX `idx_create_user`(`create_user`),
    INDEX `idx_update_user`(`update_user`),
    INDEX `idx_deleted`(`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目类型表';

-- changeset zjx:review-type-table-2
-- comment 创建类型流程配置表

CREATE TABLE IF NOT EXISTS `review_type_process_config` (
    `id`            bigint(20)   AUTO_INCREMENT       COMMENT 'ID',
    `type_id`       bigint(20)   NOT NULL             COMMENT '项目类型ID',
    `process_type`  varchar(20)  NOT NULL             COMMENT '流程类型（REVIEW=评审流程,MANAGE=管理流程）',
    `template_id`   bigint(20)   NOT NULL             COMMENT '流程模板ID（逻辑外键）',
    `template_name` varchar(100) DEFAULT NULL         COMMENT '流程模板名称（冗余，便于查询展示）',
    `is_primary`    bit(1)       NOT NULL DEFAULT b'1' COMMENT '是否主流程',
    `create_user`   bigint(20)   NOT NULL             COMMENT '创建人',
    `create_time`   datetime     NOT NULL             COMMENT '创建时间',
    `update_user`   bigint(20)   DEFAULT NULL         COMMENT '修改人',
    `update_time`   datetime     DEFAULT NULL         COMMENT '修改时间',
    `deleted`       bigint(20)   NOT NULL DEFAULT 0   COMMENT '是否已删除（0：否；id：是）',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_type_process`(`type_id`, `process_type`, `deleted`),
    INDEX `idx_type`(`type_id`, `deleted`),
    INDEX `idx_template`(`template_id`, `deleted`),
    INDEX `idx_create_user`(`create_user`),
    INDEX `idx_update_user`(`update_user`),
    INDEX `idx_deleted`(`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='类型流程配置表';

-- changeset zjx:review-type-table-3
-- comment 创建类型表单映射表

CREATE TABLE IF NOT EXISTS `review_type_form_mapping` (
    `id`                 bigint(20)   AUTO_INCREMENT COMMENT 'ID',
    `type_id`            bigint(20)   NOT NULL       COMMENT '项目类型ID',
    `mapping_type`       varchar(20)  NOT NULL       COMMENT '映射类型（REVIEW=评审流程,MANAGE=管理流程）',
    `node_type`          varchar(20)  NOT NULL       COMMENT '节点类型（APPLICATION=申请,AUDIT=审核,REVIEW=评审,DECISION=决策,STAGE=管理阶段）',
    `node_sequence`      int          DEFAULT NULL   COMMENT '节点序号（APPLICATION节点为NULL，其他节点从1开始与流程轮次/阶段序号对应）',
    `form_template_id`   bigint(20)   NOT NULL       COMMENT '表单模板ID（逻辑外键）',
    `form_template_name` varchar(100) DEFAULT NULL   COMMENT '表单模板名称（冗余）',
    `create_user`        bigint(20)   NOT NULL       COMMENT '创建人',
    `create_time`        datetime     NOT NULL       COMMENT '创建时间',
    `update_user`        bigint(20)   DEFAULT NULL   COMMENT '修改人',
    `update_time`        datetime     DEFAULT NULL   COMMENT '修改时间',
    `deleted`            bigint(20)   NOT NULL DEFAULT 0 COMMENT '是否已删除（0：否；id：是）',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_type_node`(`type_id`, `mapping_type`, `node_type`, `node_sequence`, `deleted`),
    INDEX `idx_type`(`type_id`, `deleted`),
    INDEX `idx_form_template`(`form_template_id`, `deleted`),
    INDEX `idx_mapping_type`(`mapping_type`, `deleted`),
    INDEX `idx_create_user`(`create_user`),
    INDEX `idx_update_user`(`update_user`),
    INDEX `idx_deleted`(`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='类型表单映射表';

-- changeset zjx:review-type-table-4
-- comment 创建类型人员范围配置表

CREATE TABLE IF NOT EXISTS `review_type_personnel_config` (
    `id`           bigint(20)   AUTO_INCREMENT COMMENT 'ID',
    `type_id`      bigint(20)   NOT NULL       COMMENT '项目类型ID',
    `role_type`    varchar(30)  NOT NULL       COMMENT '角色类型（AUDITOR=审核人,REVIEWER=评审人,DECISION_MAKER=决策人,MANAGER=管理人,ACCEPTANCE_INSPECTOR=验收人）',
    `scope_type`   varchar(20)  NOT NULL       COMMENT '范围类型（DEPT=部门,USER=用户,ROLE=业务角色,COMBINED=综合规则）',
    `scope_config` json         NOT NULL       COMMENT '范围配置（JSON格式，根据scope_type不同而不同）',
    `remark`       varchar(500) DEFAULT NULL   COMMENT '备注说明',
    `create_user`  bigint(20)   NOT NULL       COMMENT '创建人',
    `create_time`  datetime     NOT NULL       COMMENT '创建时间',
    `update_user`  bigint(20)   DEFAULT NULL   COMMENT '修改人',
    `update_time`  datetime     DEFAULT NULL   COMMENT '修改时间',
    `deleted`      bigint(20)   NOT NULL DEFAULT 0 COMMENT '是否已删除（0：否；id：是）',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_type_role`(`type_id`, `role_type`, `deleted`),
    INDEX `idx_type`(`type_id`, `deleted`),
    INDEX `idx_role_type`(`role_type`, `deleted`),
    INDEX `idx_create_user`(`create_user`),
    INDEX `idx_update_user`(`update_user`),
    INDEX `idx_deleted`(`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='类型人员范围配置表';

-- changeset zjx:review-type-table-5
-- comment 创建类型可见性配置表

CREATE TABLE IF NOT EXISTS `review_type_visibility_config` (
    `id`              bigint(20)   AUTO_INCREMENT COMMENT 'ID',
    `type_id`         bigint(20)   NOT NULL       COMMENT '项目类型ID',
    `visibility_type` varchar(20)  NOT NULL       COMMENT '可见类型（DEPT=部门,USER=用户,ALL=全部可见）',
    `target_id`       bigint(20)   DEFAULT NULL   COMMENT '目标ID（部门ID或用户ID，ALL类型时为NULL）',
    `target_name`     varchar(100) DEFAULT NULL   COMMENT '目标名称（冗余）',
    `create_user`     bigint(20)   NOT NULL       COMMENT '创建人',
    `create_time`     datetime     NOT NULL       COMMENT '创建时间',
    `update_user`     bigint(20)   DEFAULT NULL   COMMENT '修改人',
    `update_time`     datetime     DEFAULT NULL   COMMENT '修改时间',
    `deleted`         bigint(20)   NOT NULL DEFAULT 0 COMMENT '是否已删除（0：否；id：是）',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_type_target`(`type_id`, `visibility_type`, `target_id`, `deleted`),
    INDEX `idx_type`(`type_id`, `deleted`),
    INDEX `idx_visibility_type`(`visibility_type`, `deleted`),
    INDEX `idx_create_user`(`create_user`),
    INDEX `idx_update_user`(`update_user`),
    INDEX `idx_deleted`(`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='类型可见性配置表';

-- changeset zjx:review-type-table-6
-- comment 创建类型审批规则配置表（新增）

CREATE TABLE IF NOT EXISTS `review_type_approval_config` (
    `id`                  bigint(20)   AUTO_INCREMENT COMMENT 'ID',
    `type_id`             bigint(20)   NOT NULL       COMMENT '项目类型ID',
    `node_scope`          varchar(30)  NOT NULL       COMMENT '节点标识（如 AUDIT_1、REVIEW_1、DECISION_1、ACCEPTANCE）',
    `approval_mode`       varchar(30)  NOT NULL       COMMENT '审批模式（VOTE_ALL_PASS/VOTE_MAJORITY_PASS/VOTE_ONE_PASS/SCORE_PASS）',
    `majority_ratio`      decimal(4,2) DEFAULT 0.67   COMMENT '多数通过比例（0~1），仅 VOTE_MAJORITY_PASS 时有效，默认0.67',
    `pass_threshold`      decimal(6,2) DEFAULT NULL   COMMENT '评分通过阈值，SCORE_PASS 时必填',
    `good_threshold`      decimal(6,2) DEFAULT NULL   COMMENT '良好阈值（可选）',
    `excellent_threshold` decimal(6,2) DEFAULT NULL   COMMENT '优秀阈值（可选）',
    `score_calc_method`   varchar(20)  DEFAULT NULL   COMMENT '评分计算方式（WEIGHTED_AVG/AVG/MIN），SCORE_PASS 时必填',
    `weight_mode`         varchar(20)  DEFAULT NULL   COMMENT '评审人权重模式（EQUAL/PRESET），SCORE_PASS 时必填',
    `score_table_weights` json         DEFAULT NULL   COMMENT '多个SCORE_TABLE字段间的权重，格式：{"fieldCode":0.4}，仅一个SCORE_TABLE时为NULL',
    `reject_back_to`      varchar(20)  DEFAULT NULL   COMMENT '验收不通过回退目标，仅ACCEPTANCE节点填写：FIRST_EXECUTION或执行阶段序号（如"3"）',
    `create_user`         bigint(20)   NOT NULL       COMMENT '创建人',
    `create_time`         datetime     NOT NULL       COMMENT '创建时间',
    `update_user`         bigint(20)   DEFAULT NULL   COMMENT '修改人',
    `update_time`         datetime     DEFAULT NULL   COMMENT '修改时间',
    `deleted`             bigint(20)   NOT NULL DEFAULT 0 COMMENT '是否已删除（0：否；id：是）',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_type_node`(`type_id`, `node_scope`, `deleted`),
    INDEX `idx_type`(`type_id`, `deleted`),
    INDEX `idx_create_user`(`create_user`),
    INDEX `idx_update_user`(`update_user`),
    INDEX `idx_deleted`(`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='类型审批规则配置表';

-- changeset zjx:review-type-table-7
-- comment 创建类型评审人权重配置表（PRESET模式专用，新增）

CREATE TABLE IF NOT EXISTS `review_type_reviewer_weight` (
    `id`          bigint(20)    AUTO_INCREMENT COMMENT 'ID',
    `type_id`     bigint(20)    NOT NULL       COMMENT '项目类型ID',
    `node_scope`  varchar(30)   NOT NULL       COMMENT '节点标识（与approval_config.node_scope对应）',
    `user_id`     bigint(20)    NOT NULL       COMMENT '评审人用户ID（必须在对应角色的USER人员范围内）',
    `user_name`   varchar(100)  DEFAULT NULL   COMMENT '评审人姓名（冗余）',
    `weight`      decimal(5,4)  NOT NULL       COMMENT '权重值（0~1，同一节点所有人权重之和须=1.0）',
    `create_user` bigint(20)    NOT NULL       COMMENT '创建人',
    `create_time` datetime      NOT NULL       COMMENT '创建时间',
    `update_user` bigint(20)    DEFAULT NULL   COMMENT '修改人',
    `update_time` datetime      DEFAULT NULL   COMMENT '修改时间',
    `deleted`     bigint(20)    NOT NULL DEFAULT 0 COMMENT '是否已删除（0：否；id：是）',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_type_node_user`(`type_id`, `node_scope`, `user_id`, `deleted`),
    INDEX `idx_type_node`(`type_id`, `node_scope`, `deleted`),
    INDEX `idx_user`(`user_id`, `deleted`),
    INDEX `idx_create_user`(`create_user`),
    INDEX `idx_update_user`(`update_user`),
    INDEX `idx_deleted`(`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='类型评审人权重配置表（PRESET模式专用）';
