-- liquibase formatted sql

-- changeset zjx:add-dept-id-to-user-role
-- comment 为用户角色关联表增加部门字段，实现多部门多角色权限系统

-- 1. 增加部门字段
ALTER TABLE `sys_user_role`
ADD COLUMN `dept_id` bigint(20) DEFAULT NULL COMMENT '部门ID（为NULL表示全局角色）';

-- 2. 为现有数据填充部门ID（使用用户的当前部门）
UPDATE `sys_user_role` ur
INNER JOIN `sys_user` u ON ur.user_id = u.id
SET ur.dept_id = u.dept_id
WHERE ur.dept_id IS NULL;

-- 3. 修改唯一索引
ALTER TABLE `sys_user_role` DROP INDEX `uk_user_id_role_id`;
CREATE UNIQUE INDEX `uk_user_id_dept_id_role_id`
ON `sys_user_role`(`user_id`, `dept_id`, `role_id`);

-- rollback ALTER TABLE `sys_user_role` DROP COLUMN `dept_id`;
-- rollback ALTER TABLE `sys_user_role` DROP INDEX `uk_user_id_dept_id_role_id`;
-- rollback CREATE UNIQUE INDEX `uk_user_id_role_id` ON `sys_user_role`(`user_id`, `role_id`);
