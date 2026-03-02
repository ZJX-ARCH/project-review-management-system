-- liquibase formatted sql

-- changeset zjx:review-type-data-1
-- comment 初始化项目类型管理菜单及权限

-- 项目类型管理（页面，挂在项目评审管理下，sort=2）
INSERT INTO `sys_menu`
(`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `icon`, `is_external`, `is_cache`, `is_hidden`, `sort`, `status`, `create_user`, `create_time`)
VALUES
(1737201292001, '项目类型管理', 1737201290001, 2, '/review/type', 'ReviewType', 'review/type/index', 'appstore', b'0', b'1', b'0', 2, 1, 1, NOW());

-- 项目类型管理 - 按钮权限
INSERT INTO `sys_menu`
(`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
(1737201292011, '查询', 1737201292001, 3, 'review:type:query',  1, 1, 1, NOW()),
(1737201292012, '创建', 1737201292001, 3, 'review:type:create', 2, 1, 1, NOW()),
(1737201292013, '修改', 1737201292001, 3, 'review:type:update', 3, 1, 1, NOW()),
(1737201292014, '删除', 1737201292001, 3, 'review:type:delete', 4, 1, 1, NOW()),
(1737201292015, '启用/禁用', 1737201292001, 3, 'review:type:status', 5, 1, 1, NOW());

-- changeset zjx:review-type-data-2
-- comment 初始化示例项目类型（草稿状态）

-- 示例类型：科研项目（草稿，引用已有流程模板和表单模板）
INSERT INTO `review_project_type`
(`id`, `type_name`, `type_code`, `description`, `sort_order`, `status`, `dept_id`, `create_user`, `create_time`, `deleted`)
VALUES
(1737209001001, '科研项目', 'RESEARCH_PROJECT', '用于管理科研项目申报、评审、决策、执行、验收全生命周期', 1, 0, 1, 1, NOW(), 0);

-- 流程配置：科研项目 -> 标准三轮审核流程（评审）+ 五阶段科研流程（管理）
INSERT INTO `review_type_process_config`
(`id`, `type_id`, `process_type`, `template_id`, `template_name`, `is_primary`, `create_user`, `create_time`, `deleted`)
VALUES
(1737209101001, 1737209001001, 'REVIEW', 1737205001001, '标准三轮审核流程', b'1', 1, NOW(), 0),
(1737209101002, 1737209001001, 'MANAGE', 1737206001001, '五阶段科研流程',   b'1', 1, NOW(), 0);

-- 表单映射：评审流程节点
INSERT INTO `review_type_form_mapping`
(`id`, `type_id`, `mapping_type`, `node_type`, `node_sequence`, `form_template_id`, `form_template_name`, `create_user`, `create_time`, `deleted`)
VALUES
-- 评审流程
(1737209201001, 1737209001001, 'REVIEW', 'APPLICATION', NULL, 1737207001001, '科研项目申请表', 1, NOW(), 0),
(1737209201002, 1737209001001, 'REVIEW', 'AUDIT',       1,    1737207002001, '初审意见表',     1, NOW(), 0),
(1737209201003, 1737209001001, 'REVIEW', 'AUDIT',       2,    1737207002002, '复审意见表',     1, NOW(), 0),
(1737209201004, 1737209001001, 'REVIEW', 'AUDIT',       3,    1737207002001, '初审意见表',     1, NOW(), 0),
(1737209201005, 1737209001001, 'REVIEW', 'REVIEW',      1,    1737207003001, '专家评审表',     1, NOW(), 0),
(1737209201006, 1737209001001, 'REVIEW', 'DECISION',    1,    1737207004001, '立项决策表',     1, NOW(), 0),
-- 管理流程（对应五阶段科研流程：立项、开题、中期、结题、验收）
(1737209201007, 1737209001001, 'MANAGE', 'STAGE', 1, 1737207005001, '立项申请表',     1, NOW(), 0),
(1737209201008, 1737209001001, 'MANAGE', 'STAGE', 2, 1737207006001, '项目进度汇报表', 1, NOW(), 0),
(1737209201009, 1737209001001, 'MANAGE', 'STAGE', 3, 1737207006002, '中期检查表',     1, NOW(), 0),
(1737209201010, 1737209001001, 'MANAGE', 'STAGE', 4, 1737207006001, '项目进度汇报表', 1, NOW(), 0),
(1737209201011, 1737209001001, 'MANAGE', 'STAGE', 5, 1737207007001, '结项验收表',     1, NOW(), 0);

-- 人员配置：科研项目各角色范围
INSERT INTO `review_type_personnel_config`
(`id`, `type_id`, `role_type`, `scope_type`, `scope_config`, `remark`, `create_user`, `create_time`, `deleted`)
VALUES
(1737209301001, 1737209001001, 'AUDITOR',              'DEPT',     '{"deptIds":[1],"includeSub":false}', '科研处全体成员',     1, NOW(), 0),
(1737209301002, 1737209001001, 'REVIEWER',             'DEPT',     '{"deptIds":[1],"includeSub":true}',  '各院系评审专家',     1, NOW(), 0),
(1737209301003, 1737209001001, 'DECISION_MAKER',       'ROLE',     '{"businessRoles":["DECISION_MAKER"]}','具有决策权限的人员', 1, NOW(), 0),
(1737209301004, 1737209001001, 'MANAGER',              'COMBINED', '{"rule":"PROJECT_DEPT_LEADER"}',      '项目所在部门领导',   1, NOW(), 0),
(1737209301005, 1737209001001, 'ACCEPTANCE_INSPECTOR', 'DEPT',     '{"deptIds":[1],"includeSub":false}', '科研处验收小组',     1, NOW(), 0);

-- 可见性配置：全部可见
INSERT INTO `review_type_visibility_config`
(`id`, `type_id`, `visibility_type`, `target_id`, `target_name`, `create_user`, `create_time`, `deleted`)
VALUES
(1737209401001, 1737209001001, 'ALL', NULL, NULL, 1, NOW(), 0);

-- 审批规则配置（使用投票模式，均为多数通过）
INSERT INTO `review_type_approval_config`
(`id`, `type_id`, `node_scope`, `approval_mode`, `majority_ratio`, `create_user`, `create_time`, `deleted`)
VALUES
(1737209501001, 1737209001001, 'AUDIT_1',    'VOTE_MAJORITY_PASS', 0.67, 1, NOW(), 0),
(1737209501002, 1737209001001, 'AUDIT_2',    'VOTE_MAJORITY_PASS', 0.67, 1, NOW(), 0),
(1737209501003, 1737209001001, 'AUDIT_3',    'VOTE_ALL_PASS',      NULL, 1, NOW(), 0),
(1737209501004, 1737209001001, 'REVIEW_1',   'VOTE_MAJORITY_PASS', 0.67, 1, NOW(), 0),
(1737209501005, 1737209001001, 'DECISION_1', 'VOTE_ALL_PASS',      NULL, 1, NOW(), 0),
(1737209501006, 1737209001001, 'ACCEPTANCE', 'VOTE_MAJORITY_PASS', 0.67, 1, NOW(), 0);

-- rollback DELETE FROM `review_type_approval_config`   WHERE `type_id` = 1737209001001;
-- rollback DELETE FROM `review_type_visibility_config` WHERE `type_id` = 1737209001001;
-- rollback DELETE FROM `review_type_personnel_config`  WHERE `type_id` = 1737209001001;
-- rollback DELETE FROM `review_type_form_mapping`      WHERE `type_id` = 1737209001001;
-- rollback DELETE FROM `review_type_process_config`    WHERE `type_id` = 1737209001001;
-- rollback DELETE FROM `review_project_type`           WHERE `id`      = 1737209001001;
-- rollback DELETE FROM `sys_menu` WHERE `id` IN (1737201292001,1737201292011,1737201292012,1737201292013,1737201292014,1737201292015);
