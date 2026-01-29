-- liquibase formatted sql

-- changeset zjx:1
-- comment 初始化流程模板管理模块数据

-- 初始化评审流程模板
INSERT INTO `review_process_template`
(`id`, `template_name`, `template_code`, `description`, `audit_rounds`, `review_rounds`, `decision_rounds`, `is_public`, `status`, `sort`, `create_user`, `create_time`, `deleted`)
VALUES
(1737205001001, '标准三轮审核流程', 'THREE_ROUND_AUDIT', '适用于重大项目的标准审核流程', 3, 1, 1, b'1', 1, 1, 1, NOW(), 0);

-- 初始化轮次名称
INSERT INTO `review_process_template_round_name`
(`id`, `template_id`, `round_type`, `round_sequence`, `round_name`, `create_user`, `create_time`, `deleted`)
VALUES
(1737205101001, 1737205001001, 'AUDIT', 1, '初审', 1, NOW(), 0),
(1737205101002, 1737205001001, 'AUDIT', 2, '复审', 1, NOW(), 0),
(1737205101003, 1737205001001, 'AUDIT', 3, '终审', 1, NOW(), 0),
(1737205101004, 1737205001001, 'REVIEW', 1, '专家评审', 1, NOW(), 0),
(1737205101005, 1737205001001, 'DECISION', 1, '最终决策', 1, NOW(), 0);

-- 初始化管理流程模板
INSERT INTO `review_management_template`
(`id`, `template_name`, `template_code`, `description`, `is_public`, `status`, `sort`, `create_user`, `create_time`, `deleted`)
VALUES
(1737206001001, '五阶段科研流程', 'FIVE_STAGE_RESEARCH', '适用于科研项目的标准管理流程', b'1', 1, 1, 1, NOW(), 0);

-- 初始化管理流程阶段（注意：计划天数由类型层配置，模板层不设置）
INSERT INTO `review_management_stage`
(`id`, `template_id`, `stage_name`, `stage_type`, `stage_order`, `is_required`, `create_user`, `create_time`, `deleted`)
VALUES
(1737206101001, 1737206001001, '立项阶段', 'KICKOFF', 1, b'1', 1, NOW(), 0),
(1737206101002, 1737206001001, '开题阶段', 'EXECUTION', 2, b'1', 1, NOW(), 0),
(1737206101003, 1737206001001, '中期检查', 'EXECUTION', 3, b'1', 1, NOW(), 0),
(1737206101004, 1737206001001, '结题阶段', 'EXECUTION', 4, b'1', 1, NOW(), 0),
(1737206101005, 1737206001001, '验收阶段', 'ACCEPTANCE', 5, b'1', 1, NOW(), 0);

-- changeset zjx:2
-- comment 初始化流程模板管理菜单

-- 项目评审管理（顶级目录）
INSERT INTO `sys_menu`
(`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `icon`, `is_external`, `is_cache`, `is_hidden`, `sort`, `status`, `create_user`, `create_time`)
VALUES
(1737201290001, '项目评审管理', 0, 1, '/review', 'Review', 'Layout', 'check-square', b'0', b'0', b'0', 9, 1, 1, NOW());

-- 流程模板管理（二级目录）
INSERT INTO `sys_menu`
(`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `icon`, `is_external`, `is_cache`, `is_hidden`, `sort`, `status`, `create_user`, `create_time`)
VALUES
(1737201291001, '流程模板管理', 1737201290001, 1, '/review/template', 'ReviewTemplate', NULL, 'file-text', b'0', b'0', b'0', 1, 1, 1, NOW());

-- 评审流程模板（菜单）
INSERT INTO `sys_menu`
(`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `icon`, `is_external`, `is_cache`, `is_hidden`, `sort`, `status`, `create_user`, `create_time`)
VALUES
(1737201291101, '评审流程模板', 1737201291001, 2, '/review/template/process', 'ProcessTemplate', 'review/template/process/index', 'layers', b'0', b'1', b'0', 1, 1, 1, NOW());

-- 评审流程模板 - 按钮权限
INSERT INTO `sys_menu`
(`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
(1737201291111, '查询', 1737201291101, 3, 'review:template:process:query', 1, 1, 1, NOW()),
(1737201291112, '创建', 1737201291101, 3, 'review:template:process:create', 2, 1, 1, NOW()),
(1737201291113, '修改', 1737201291101, 3, 'review:template:process:update', 3, 1, 1, NOW()),
(1737201291114, '删除', 1737201291101, 3, 'review:template:process:delete', 4, 1, 1, NOW()),
(1737201291115, '启用/禁用', 1737201291101, 3, 'review:template:process:status', 5, 1, 1, NOW());

-- 管理流程模板（菜单）
INSERT INTO `sys_menu`
(`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `icon`, `is_external`, `is_cache`, `is_hidden`, `sort`, `status`, `create_user`, `create_time`)
VALUES
(1737201291201, '管理流程模板', 1737201291001, 2, '/review/template/management', 'ManagementTemplate', 'review/template/management/index', 'layers', b'0', b'1', b'0', 2, 1, 1, NOW());

-- 管理流程模板 - 按钮权限
INSERT INTO `sys_menu`
(`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
(1737201291211, '查询', 1737201291201, 3, 'review:template:management:query', 1, 1, 1, NOW()),
(1737201291212, '创建', 1737201291201, 3, 'review:template:management:create', 2, 1, 1, NOW()),
(1737201291213, '修改', 1737201291201, 3, 'review:template:management:update', 3, 1, 1, NOW()),
(1737201291214, '删除', 1737201291201, 3, 'review:template:management:delete', 4, 1, 1, NOW()),
(1737201291215, '启用/禁用', 1737201291201, 3, 'review:template:management:status', 5, 1, 1, NOW());
