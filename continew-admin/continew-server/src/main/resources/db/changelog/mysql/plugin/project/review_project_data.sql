-- liquibase formatted sql

-- changeset zjx:review-project-data-1
-- comment 初始化项目管理菜单及权限

-- 项目管理（页面，挂在项目评审管理下，sort=3）
INSERT INTO `sys_menu`
(`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `icon`, `is_external`, `is_cache`, `is_hidden`, `sort`, `status`, `create_user`, `create_time`)
VALUES
(1737201293001, '项目管理', 1737201290001, 2, '/review/project', 'ReviewProject', 'review/project/index', 'project', b'0', b'1', b'0', 3, 1, 1, NOW());

-- 项目管理 - 按钮权限
INSERT INTO `sys_menu`
(`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
(1737201293011, '查询',     1737201293001, 3, 'review:project:query',    1, 1, 1, NOW()),
(1737201293012, '创建草稿', 1737201293001, 3, 'review:project:add',      2, 1, 1, NOW()),
(1737201293013, '提交申请', 1737201293001, 3, 'review:project:submit',   3, 1, 1, NOW()),
(1737201293014, '修改申请', 1737201293001, 3, 'review:project:update',   4, 1, 1, NOW()),
(1737201293015, '撤销申请', 1737201293001, 3, 'review:project:revoke',   5, 1, 1, NOW()),
(1737201293016, '终止项目', 1737201293001, 3, 'review:project:terminate', 6, 1, 1, NOW());

-- rollback DELETE FROM `sys_menu` WHERE `id` IN (1737201293001,1737201293011,1737201293012,1737201293013,1737201293014,1737201293015,1737201293016);

-- changeset zjx:review-project-data-2
-- comment 初始化我的任务菜单及权限

-- 我的任务（页面，挂在项目评审管理下，sort=4）
INSERT INTO `sys_menu`
(`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `icon`, `is_external`, `is_cache`, `is_hidden`, `sort`, `status`, `create_user`, `create_time`)
VALUES
(1737201294001, '我的任务', 1737201290001, 2, '/review/task', 'ReviewTask', 'review/task/index', 'check-circle', b'0', b'1', b'0', 4, 1, 1, NOW());

-- 我的任务 - 按钮权限
INSERT INTO `sys_menu`
(`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
(1737201294011, '查询任务',   1737201294001, 3, 'review:task:query',    1, 1, 1, NOW()),
(1737201294012, '提交任务',   1737201294001, 3, 'review:task:submit',   2, 1, 1, NOW()),
(1737201294013, '暂存任务',   1737201294001, 3, 'review:task:save',     3, 1, 1, NOW()),
(1737201294014, '转办任务',   1737201294001, 3, 'review:task:transfer', 4, 1, 1, NOW());

-- rollback DELETE FROM `sys_menu` WHERE `id` IN (1737201294001,1737201294011,1737201294012,1737201294013,1737201294014);

-- changeset zjx:review-project-data-3
-- comment 添加项目管理子路由页面（隐藏菜单：创建、详情）

INSERT INTO `sys_menu`
(`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `is_external`, `is_cache`, `is_hidden`, `sort`, `status`, `create_user`, `create_time`)
VALUES
-- 创建项目页（两步向导，is_cache=0 避免缓存草稿状态）
(1737201293021, '提交新项目', 1737201293001, 2, '/review/project/create',      'ReviewProjectCreate', 'review/project/create',        b'0', b'0', b'1', 11, 1, 1, NOW()),
-- 项目详情页（申请人视角）
(1737201293022, '项目详情',   1737201293001, 2, '/review/project/detail/:id',  'ReviewProjectDetail', 'review/project/detail/[id]',   b'0', b'0', b'1', 12, 1, 1, NOW()),
-- 任务详情页（处理人视角）
(1737201294021, '任务详情',   1737201294001, 2, '/review/task/detail/:id',     'ReviewTaskDetail',    'review/task/detail/[id]',      b'0', b'0', b'1', 11, 1, 1, NOW());

-- rollback DELETE FROM `sys_menu` WHERE `id` IN (1737201293021,1737201293022,1737201294021);
