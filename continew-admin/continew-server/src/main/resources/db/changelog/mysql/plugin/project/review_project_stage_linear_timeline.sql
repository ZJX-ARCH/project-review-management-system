-- liquibase formatted sql

-- changeset zjx:add-stage-execution-tracking-1
-- comment: 为 review_project_stage 表添加线性时间线支持字段
ALTER TABLE review_project_stage ADD COLUMN original_stage_order INT NULL COMMENT '原始阶段序号（模板中的序号）' AFTER stage_order;
ALTER TABLE review_project_stage ADD COLUMN execution_sequence INT NOT NULL DEFAULT 1 COMMENT '执行序号（同一原始阶段的第N次执行）' AFTER original_stage_order;
ALTER TABLE review_project_stage ADD COLUMN reject_remark VARCHAR(500) NULL COMMENT '驳回原因' AFTER is_overdue;

-- changeset zjx:add-stage-execution-tracking-2
-- comment: 为现有记录填充 original_stage_order 默认值
UPDATE review_project_stage SET original_stage_order = stage_order WHERE original_stage_order IS NULL;
ALTER TABLE review_project_stage MODIFY COLUMN original_stage_order INT NOT NULL COMMENT '原始阶段序号（模板中的序号）';
