-- liquibase formatted sql

-- changeset zjx:review_project_stage_history_enhancement-1
-- comment: 扩展阶段历史表，支持驳回历史快照记录
ALTER TABLE review_project_stage_history
    ADD COLUMN project_id BIGINT COMMENT '项目ID' AFTER stage_id,
    ADD COLUMN stage_type VARCHAR(20) COMMENT '阶段类型' AFTER project_id,
    ADD COLUMN stage_order INT COMMENT '阶段序号' AFTER stage_type,
    ADD COLUMN stage_name VARCHAR(100) COMMENT '阶段名称' AFTER stage_order,
    ADD COLUMN start_date DATE COMMENT '开始日期' AFTER new_status,
    ADD COLUMN deadline DATE COMMENT '截止日期' AFTER start_date,
    ADD COLUMN stage_form_data JSON COMMENT '阶段成果表单数据' AFTER deadline;

-- changeset zjx:review_project_stage_history_enhancement-2
-- comment: 添加索引优化查询性能
CREATE INDEX idx_project_stage ON review_project_stage_history(project_id, stage_order);
