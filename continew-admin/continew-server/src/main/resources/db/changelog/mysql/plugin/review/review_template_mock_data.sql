-- liquibase formatted sql

-- changeset zjx:add-review-template-mock-data
-- comment 添加流程模板管理模块的模拟数据

-- ================================
-- 评审流程模板（2个）
-- ================================

-- 1. 单轮审核流程（适用于小型项目或紧急项目）
INSERT INTO `review_process_template`
(`id`, `template_name`, `template_code`, `description`, `audit_rounds`, `review_rounds`, `decision_rounds`, `status`, `sort`, `create_user`, `create_time`, `deleted`)
VALUES
(1737205001002, '单轮审核流程', 'PROC_SINGLE_AUDIT', '适用于小型项目或紧急项目的快速审批流程', 1, 0, 1, 1, 2, 1, NOW(), 0);

-- 单轮审核流程 - 轮次名称
INSERT INTO `review_process_template_round_name`
(`id`, `template_id`, `round_type`, `round_sequence`, `round_name`, `create_user`, `create_time`, `deleted`)
VALUES
(1737205102001, 1737205001002, 'AUDIT', 1, '部门审核', 1, NOW(), 0),
(1737205102002, 1737205001002, 'DECISION', 1, '领导决策', 1, NOW(), 0);

-- 2. 五轮评审流程（适用于高风险或高投资项目）
INSERT INTO `review_process_template`
(`id`, `template_name`, `template_code`, `description`, `audit_rounds`, `review_rounds`, `decision_rounds`, `status`, `sort`, `create_user`, `create_time`, `deleted`)
VALUES
(1737205001003, '五轮评审流程', 'PROC_FIVE_REVIEW', '适用于高风险或高投资项目的严格审批流程', 2, 2, 1, 1, 3, 1, NOW(), 0);

-- 五轮评审流程 - 轮次名称
INSERT INTO `review_process_template_round_name`
(`id`, `template_id`, `round_type`, `round_sequence`, `round_name`, `create_user`, `create_time`, `deleted`)
VALUES
(1737205103001, 1737205001003, 'AUDIT', 1, '部门初审', 1, NOW(), 0),
(1737205103002, 1737205001003, 'AUDIT', 2, '财务审核', 1, NOW(), 0),
(1737205103003, 1737205001003, 'REVIEW', 1, '技术专家评审', 1, NOW(), 0),
(1737205103004, 1737205001003, 'REVIEW', 2, '外部专家评审', 1, NOW(), 0),
(1737205103005, 1737205001003, 'DECISION', 1, '董事会决策', 1, NOW(), 0);

-- ================================
-- 管理流程模板（2个）
-- ================================

-- 3. 敏捷迭代流程（适用于软件开发项目）
INSERT INTO `review_management_template`
(`id`, `template_name`, `template_code`, `description`, `status`, `sort`, `create_user`, `create_time`, `deleted`)
VALUES
(1737206001002, '敏捷迭代流程', 'MGMT_AGILE_SPRINT', '适用于软件开发项目的敏捷迭代管理流程', 1, 2, 1, NOW(), 0);

-- 敏捷迭代流程 - 阶段配置
INSERT INTO `review_management_stage`
(`id`, `template_id`, `stage_name`, `stage_type`, `stage_order`, `is_required`, `create_user`, `create_time`, `deleted`)
VALUES
(1737206102001, 1737206001002, '项目启动', 'KICKOFF', 1, b'1', 1, NOW(), 0),
(1737206102002, 1737206001002, 'Sprint 1', 'EXECUTION', 2, b'1', 1, NOW(), 0),
(1737206102003, 1737206001002, 'Sprint 2', 'EXECUTION', 3, b'1', 1, NOW(), 0),
(1737206102004, 1737206001002, 'Sprint 3', 'EXECUTION', 4, b'0', 1, NOW(), 0),
(1737206102005, 1737206001002, 'UAT测试', 'ACCEPTANCE', 5, b'1', 1, NOW(), 0),
(1737206102006, 1737206001002, '项目交付', 'ACCEPTANCE', 6, b'1', 1, NOW(), 0);

-- 4. 瀑布式SDLC流程（适用于传统软件工程项目）
INSERT INTO `review_management_template`
(`id`, `template_name`, `template_code`, `description`, `status`, `sort`, `create_user`, `create_time`, `deleted`)
VALUES
(1737206001003, '瀑布式SDLC流程', 'MGMT_WATERFALL_SDLC', '适用于传统软件工程项目的瀑布式开发生命周期', 1, 3, 1, NOW(), 0);

-- 瀑布式SDLC流程 - 阶段配置
INSERT INTO `review_management_stage`
(`id`, `template_id`, `stage_name`, `stage_type`, `stage_order`, `is_required`, `create_user`, `create_time`, `deleted`)
VALUES
(1737206103001, 1737206001003, '项目立项', 'KICKOFF', 1, b'1', 1, NOW(), 0),
(1737206103002, 1737206001003, '需求分析', 'EXECUTION', 2, b'1', 1, NOW(), 0),
(1737206103003, 1737206001003, '系统设计', 'EXECUTION', 3, b'1', 1, NOW(), 0),
(1737206103004, 1737206001003, '编码实现', 'EXECUTION', 4, b'1', 1, NOW(), 0),
(1737206103005, 1737206001003, '系统测试', 'EXECUTION', 5, b'1', 1, NOW(), 0),
(1737206103006, 1737206001003, '试运行', 'ACCEPTANCE', 6, b'1', 1, NOW(), 0),
(1737206103007, 1737206001003, '项目验收', 'ACCEPTANCE', 7, b'1', 1, NOW(), 0);

-- rollback DELETE FROM `review_process_template_round_name` WHERE `template_id` IN (1737205001002, 1737205001003);
-- rollback DELETE FROM `review_process_template` WHERE `id` IN (1737205001002, 1737205001003);
-- rollback DELETE FROM `review_management_stage` WHERE `template_id` IN (1737206001002, 1737206001003);
-- rollback DELETE FROM `review_management_template` WHERE `id` IN (1737206001002, 1737206001003);
