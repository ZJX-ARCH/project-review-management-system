-- liquibase formatted sql

-- changeset zjx:review-template-add-dept-id-1
-- comment 为评审模板相关表添加部门ID字段以支持数据权限

-- 为评审流程模板表添加部门ID字段
ALTER TABLE `review_process_template`
ADD COLUMN `dept_id` bigint(20) NOT NULL DEFAULT 1 COMMENT '部门ID' AFTER `decision_rounds`;

-- 添加部门ID索引
CREATE INDEX `idx_dept_id` ON `review_process_template`(`dept_id`, `deleted`);

-- 为管理流程模板表添加部门ID字段
ALTER TABLE `review_management_template`
ADD COLUMN `dept_id` bigint(20) NOT NULL DEFAULT 1 COMMENT '部门ID' AFTER `description`;

-- 添加部门ID索引
CREATE INDEX `idx_dept_id` ON `review_management_template`(`dept_id`, `deleted`);
