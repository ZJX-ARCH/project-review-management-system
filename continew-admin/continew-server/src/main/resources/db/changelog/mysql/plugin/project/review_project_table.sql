-- liquibase formatted sql

-- changeset zjx:review-project-table-1
-- comment 创建项目实例主表

CREATE TABLE IF NOT EXISTS `review_project` (
    `id`                     bigint(20)    NOT NULL                    COMMENT '项目ID（雪花ID）',
    `type_id`                bigint(20)    NOT NULL                    COMMENT '项目类型ID',
    `dept_id`                bigint(20)    NOT NULL                    COMMENT '申请人所属部门ID',
    `project_name`           varchar(200)  NOT NULL                    COMMENT '项目名称',
    `description`            text          DEFAULT NULL                COMMENT '项目描述',
    `status`                 int           NOT NULL                    COMMENT '当前状态（对应 ProjectStatus 枚举 Integer value，如1=草稿/2=已提交/10=审核中）',
    `current_node_type`      varchar(20)   DEFAULT NULL                COMMENT '当前节点类型（AUDIT/REVIEW/DECISION/KICKOFF/EXECUTION/ACCEPTANCE）',
    `current_node_sequence`  int           DEFAULT NULL                COMMENT '当前节点序号',
    `snapshot_config`        json          DEFAULT NULL                COMMENT '提交时的类型配置快照（类型参数固化）',
    `application_form_data`  json          DEFAULT NULL                COMMENT '申请表单填写数据',
    `applicant_id`           bigint(20)    NOT NULL                    COMMENT '申请人用户ID',
    `submitted_time`         datetime      DEFAULT NULL                COMMENT '提交时间（从 DRAFT 变为 SUBMITTED 时记录）',
    `create_user`            bigint(20)    NOT NULL                    COMMENT '创建人',
    `create_time`            datetime      NOT NULL                    COMMENT '创建时间',
    `update_user`            bigint(20)    DEFAULT NULL                COMMENT '修改人',
    `update_time`            datetime      DEFAULT NULL                COMMENT '修改时间',
    `deleted`                bigint(20)    NOT NULL DEFAULT 0          COMMENT '是否已删除（0：否；id：是）',
    PRIMARY KEY (`id`),
    INDEX `idx_applicant`(`applicant_id`, `status`, `deleted`),
    INDEX `idx_type`(`type_id`, `deleted`),
    INDEX `idx_dept`(`dept_id`, `deleted`),
    INDEX `idx_status`(`status`, `deleted`),
    INDEX `idx_create_user`(`create_user`),
    INDEX `idx_update_user`(`update_user`),
    INDEX `idx_deleted`(`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目实例主表';

-- changeset zjx:review-project-table-2
-- comment 创建任务实例表

CREATE TABLE IF NOT EXISTS `review_task` (
    `id`                        bigint(20)    NOT NULL                    COMMENT '任务ID（雪花ID）',
    `project_id`                bigint(20)    NOT NULL                    COMMENT '项目ID',
    `task_type`                 varchar(20)   NOT NULL                    COMMENT '任务类型（AUDIT/REVIEW/DECISION/MANAGEMENT/ACCEPTANCE）',
    `node_sequence`             int           DEFAULT NULL                COMMENT '节点序号（轮次序号或阶段序号）',
    `assignee_id`               bigint(20)    NOT NULL                    COMMENT '被分配人用户ID',
    `status`                    varchar(20)   NOT NULL DEFAULT 'PENDING'  COMMENT '任务状态（PENDING/SAVED/COMPLETED/TRANSFERRED/CANCELLED）',
    `decision`                  varchar(20)   DEFAULT NULL                COMMENT '决策结果（PASS/REJECT/UNQUALIFIED/WITHDRAW）',
    `score`                     decimal(6,2)  DEFAULT NULL                COMMENT '评分（SCORE_PASS 模式时填写）',
    `form_data`                 json          DEFAULT NULL                COMMENT '任务表单填写数据',
    `reject_back_to_stage_order` int          DEFAULT NULL                COMMENT '验收任务 REJECT 时指定回退到的阶段序号',
    `transfer_count`            int           NOT NULL DEFAULT 0          COMMENT '转办次数（上限 MAX_TRANSFER_COUNT=3）',
    `transfer_remark`           varchar(500)  DEFAULT NULL                COMMENT '转办说明（转办时填写，留存最后一次转办原因）',
    `assign_time`               datetime      DEFAULT NULL                COMMENT '分配时间',
    `complete_time`             datetime      DEFAULT NULL                COMMENT '完成时间',
    `create_user`               bigint(20)    NOT NULL                    COMMENT '创建人',
    `create_time`               datetime      NOT NULL                    COMMENT '创建时间',
    `update_user`               bigint(20)    DEFAULT NULL                COMMENT '修改人',
    `update_time`               datetime      DEFAULT NULL                COMMENT '修改时间',
    `deleted`                   bigint(20)    NOT NULL DEFAULT 0          COMMENT '是否已删除（0：否；id：是）',
    PRIMARY KEY (`id`),
    INDEX `idx_assignee`(`assignee_id`, `status`, `deleted`),
    INDEX `idx_project_node`(`project_id`, `task_type`, `node_sequence`, `deleted`),
    INDEX `idx_project`(`project_id`, `deleted`),
    INDEX `idx_task_type`(`task_type`, `status`, `deleted`),
    INDEX `idx_create_user`(`create_user`),
    INDEX `idx_update_user`(`update_user`),
    INDEX `idx_deleted`(`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务实例表';

-- changeset zjx:review-project-table-3
-- comment 创建管理阶段实例表

CREATE TABLE IF NOT EXISTS `review_project_stage` (
    `id`               bigint(20)    NOT NULL                    COMMENT '阶段ID（雪花ID）',
    `project_id`       bigint(20)    NOT NULL                    COMMENT '项目ID',
    `stage_type`       varchar(20)   NOT NULL                    COMMENT '阶段类型（KICKOFF/EXECUTION/ACCEPTANCE）',
    `stage_order`      int           NOT NULL                    COMMENT '阶段序号（来自管理流程模板 stage_order）',
    `stage_name`       varchar(100)  NOT NULL                    COMMENT '阶段名称（来自快照）',
    `planned_days`     int           DEFAULT NULL                COMMENT '计划天数（来自快照，暂未被模板模块支持，预留）',
    `start_date`       date          DEFAULT NULL                COMMENT '实际开始日期（阶段激活时设置）',
    `deadline`         date          DEFAULT NULL                COMMENT '计划完成日期（start_date + planned_days，planned_days 不为空时计算）',
    `status`           varchar(20)   NOT NULL DEFAULT 'PENDING'  COMMENT '阶段状态（PENDING/IN_PROGRESS/SUBMITTED/COMPLETED/REJECTED）',
    `is_overdue`       tinyint(1)    NOT NULL DEFAULT 0          COMMENT '是否超时（0否1是，定时任务维护）',
    `stage_form_data`  json          DEFAULT NULL                COMMENT '申请人提交的阶段成果表单数据（重做时直接覆盖）',
    `create_user`      bigint(20)    NOT NULL                    COMMENT '创建人',
    `create_time`      datetime      NOT NULL                    COMMENT '创建时间',
    `update_user`      bigint(20)    DEFAULT NULL                COMMENT '修改人',
    `update_time`      datetime      DEFAULT NULL                COMMENT '修改时间',
    `deleted`          bigint(20)    NOT NULL DEFAULT 0          COMMENT '是否已删除（0：否；id：是）',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_project_stage_order`(`project_id`, `stage_order`, `deleted`),
    INDEX `idx_project`(`project_id`, `status`, `deleted`),
    INDEX `idx_stage_type`(`stage_type`, `status`, `deleted`),
    INDEX `idx_create_user`(`create_user`),
    INDEX `idx_update_user`(`update_user`),
    INDEX `idx_deleted`(`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理阶段实例表';

-- changeset zjx:review-project-table-4
-- comment 创建申请表单修改痕迹日志表

CREATE TABLE IF NOT EXISTS `review_project_modify_log` (
    `id`             bigint(20)    NOT NULL           COMMENT '日志ID（雪花ID）',
    `project_id`     bigint(20)    NOT NULL           COMMENT '项目ID',
    `modified_by`    bigint(20)    NOT NULL           COMMENT '修改人用户ID（实际发起修改的人，可能是管理员代改）',
    `old_form_data`  json          DEFAULT NULL       COMMENT '修改前申请表单数据',
    `new_form_data`  json          DEFAULT NULL       COMMENT '修改后申请表单数据',
    `modify_reason`  text          DEFAULT NULL       COMMENT '修改原因（可选）',
    `create_user`    bigint(20)    NOT NULL           COMMENT '创建人',
    `create_time`    datetime      NOT NULL           COMMENT '创建时间',
    `update_user`    bigint(20)    DEFAULT NULL       COMMENT '修改人',
    `update_time`    datetime      DEFAULT NULL       COMMENT '修改时间',
    `deleted`        bigint(20)    NOT NULL DEFAULT 0 COMMENT '是否已删除（0：否；id：是）',
    PRIMARY KEY (`id`),
    INDEX `idx_project`(`project_id`, `deleted`),
    INDEX `idx_modified_by`(`modified_by`),
    INDEX `idx_create_user`(`create_user`),
    INDEX `idx_deleted`(`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='申请表单修改痕迹日志表';
