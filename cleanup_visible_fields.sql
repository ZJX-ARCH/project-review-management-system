-- 删除流程模板表中的可见范围相关字段
ALTER TABLE `review_process_template`
    DROP COLUMN `dept_ids`,
    DROP COLUMN `role_ids`,
    DROP COLUMN `is_public`;

-- 删除管理模板表中的可见范围相关字段
ALTER TABLE `review_management_template`
    DROP COLUMN `dept_ids`,
    DROP COLUMN `role_ids`,
    DROP COLUMN `is_public`;

-- 删除相关索引（如果存在）
ALTER TABLE `review_process_template` DROP INDEX IF EXISTS `idx_public`;
ALTER TABLE `review_management_template` DROP INDEX IF EXISTS `idx_public`;
