-- liquibase formatted sql

-- changeset zjx:review-type-approval-add-reviewer-count-1
-- comment 审批规则表新增审批人数字段

ALTER TABLE `review_type_approval_config`
    ADD COLUMN `required_reviewer_count` int DEFAULT NULL COMMENT '所需审批人数（用于校验人员范围是否满足要求）' AFTER `node_scope`;
