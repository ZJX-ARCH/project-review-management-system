-- liquibase formatted sql

-- changeset zjx:form-template-routes-1
-- comment 添加表单模板设计器页面路由

-- 表单设计器页面（隐藏菜单）
INSERT INTO `sys_menu`
(`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `is_external`, `is_cache`, `is_hidden`, `sort`, `status`, `create_user`, `create_time`)
VALUES
(1737201291321, '表单设计器', 1737201291301, 2, '/review/template/form/designer/:id?', 'FormDesigner', 'review/template/form/designer', b'0', b'0', b'1', 6, 1, 1, NOW());
