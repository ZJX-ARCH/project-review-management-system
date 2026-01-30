-- liquibase formatted sql

-- changeset zjx:1
-- comment 添加流程模板编辑页面路由

-- 评审流程模板 - 新建/编辑页面（隐藏菜单）
INSERT INTO `sys_menu`
(`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `is_external`, `is_cache`, `is_hidden`, `sort`, `status`, `create_user`, `create_time`)
VALUES
(1737201291121, '新建/编辑评审流程模板', 1737201291101, 2, '/review/template/process/edit/:id?', 'ProcessTemplateEdit', 'review/template/process/edit', b'0', b'0', b'1', 5, 1, 1, NOW());

-- 管理流程模板 - 新建/编辑页面（隐藏菜单）
INSERT INTO `sys_menu`
(`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `is_external`, `is_cache`, `is_hidden`, `sort`, `status`, `create_user`, `create_time`)
VALUES
(1737201291221, '新建/编辑管理流程模板', 1737201291201, 2, '/review/template/management/edit/:id?', 'ManagementTemplateEdit', 'review/template/management/edit', b'0', b'0', b'1', 5, 1, 1, NOW());
