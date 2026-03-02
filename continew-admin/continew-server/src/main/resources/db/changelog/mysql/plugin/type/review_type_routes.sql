-- liquibase formatted sql

-- changeset zjx:review-type-routes-1
-- comment 添加项目类型配置向导页面路由（隐藏菜单）

-- 项目类型配置向导页面（隐藏，支持新建和编辑两种入口）
INSERT INTO `sys_menu`
(`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `is_external`, `is_cache`, `is_hidden`, `sort`, `status`, `create_user`, `create_time`)
VALUES
(1737201292021, '项目类型配置向导', 1737201292001, 2, '/review/type/configure/:id?', 'ReviewTypeConfigure', 'review/type/configure', b'0', b'0', b'1', 6, 1, 1, NOW());

-- rollback DELETE FROM `sys_menu` WHERE `id` = 1737201292021;
