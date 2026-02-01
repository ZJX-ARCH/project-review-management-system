-- liquibase formatted sql

-- changeset zjx:form-template-add-dept-id-1
-- comment 为表单模板相关表添加部门ID字段以支持数据权限

-- 为表单模板主表添加部门ID字段
ALTER TABLE `review_form_template`
ADD COLUMN `dept_id` bigint(20) NOT NULL DEFAULT 1 COMMENT '部门ID' AFTER `template_type`;

-- 添加部门ID索引
CREATE INDEX `idx_dept_id` ON `review_form_template`(`dept_id`, `deleted`);
