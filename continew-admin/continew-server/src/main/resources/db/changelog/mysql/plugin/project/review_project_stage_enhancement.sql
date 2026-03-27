-- liquibase formatted sql

-- changeset zjx:add-stage-submitter-id-1
-- comment: 为 review_project_stage 表添加 submitter_id 字段，用于 STAGE_SUBMISSION 任务转办场景
ALTER TABLE review_project_stage ADD COLUMN submitter_id BIGINT NULL COMMENT '阶段提交人ID（STAGE_SUBMISSION任务转办时更新）' AFTER stage_form_data;

-- changeset zjx:create-stage-history-table-1
-- comment: 创建阶段状态历史表，记录阶段状态变更历史（如 REJECTED → IN_PROGRESS）
CREATE TABLE review_project_stage_history (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    stage_id BIGINT NOT NULL COMMENT '阶段ID',
    old_status VARCHAR(20) NOT NULL COMMENT '原状态',
    new_status VARCHAR(20) NOT NULL COMMENT '新状态',
    change_time DATETIME NOT NULL COMMENT '变更时间',
    remark VARCHAR(500) NULL COMMENT '备注',
    PRIMARY KEY (id),
    INDEX idx_stage_id (stage_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='阶段状态历史表';
