-- liquibase formatted sql

-- changeset zjx:add-show-in-tabs-to-menu
-- comment 为菜单表增加页签显示控制字段，用于控制菜单页面是否在顶部页签栏中显示

-- 1. 增加页签显示控制字段
ALTER TABLE `sys_menu`
ADD COLUMN `show_in_tabs` TINYINT(1) DEFAULT 1 COMMENT '是否在页签中显示（1是 0否）';

-- 2. 为编辑类页面设置不显示在页签中（仅针对菜单类型，type=2）
-- 评审流程模板编辑页面
UPDATE `sys_menu`
SET `show_in_tabs` = 0
WHERE `type` = 2
  AND (`path` LIKE '/review/template/process/edit%' OR `name` = 'ProcessTemplateEdit');

-- 管理流程模板编辑页面
UPDATE `sys_menu`
SET `show_in_tabs` = 0
WHERE `type` = 2
  AND (`path` LIKE '/review/template/management/edit%' OR `name` = 'ManagementTemplateEdit');

-- rollback ALTER TABLE `sys_menu` DROP COLUMN `show_in_tabs`;
