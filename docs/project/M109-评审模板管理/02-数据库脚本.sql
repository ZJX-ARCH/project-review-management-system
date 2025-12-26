-- liquibase formatted sql

-- changeset system:prj_evaluation_template_001
-- comment 创建评审模板主表
CREATE TABLE `prj_evaluation_template` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `code` VARCHAR(20) NOT NULL COMMENT '模板编码',
    `name` VARCHAR(50) NOT NULL COMMENT '模板名称',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '模板说明',
    `total_score` DECIMAL(5, 2) NOT NULL DEFAULT 100.00 COMMENT '总分',
    `item_count` INT NOT NULL DEFAULT 0 COMMENT '评分项数量',
    `category_count` INT NOT NULL DEFAULT 0 COMMENT '评分大类数量',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（1启用 2停用）',
    `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `create_user` BIGINT NOT NULL COMMENT '创建人',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `update_user` BIGINT DEFAULT NULL COMMENT '修改人',
    `update_time` DATETIME DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评审模板表';

-- changeset system:prj_evaluation_template_item_001
-- comment 创建评审模板评分项表
CREATE TABLE `prj_evaluation_template_item` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `template_id` BIGINT NOT NULL COMMENT '模板ID',
    `category_name` VARCHAR(100) NOT NULL COMMENT '大类名称',
    `category_sort` INT NOT NULL DEFAULT 0 COMMENT '大类排序',
    `item_name` VARCHAR(100) NOT NULL COMMENT '评分项名称',
    `item_sort` INT NOT NULL DEFAULT 0 COMMENT '评分项排序',
    `max_score` DECIMAL(5, 2) NOT NULL COMMENT '满分值',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '评分说明',
    `create_user` BIGINT NOT NULL COMMENT '创建人',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `update_user` BIGINT DEFAULT NULL COMMENT '修改人',
    `update_time` DATETIME DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_template_id` (`template_id`),
    KEY `idx_category_sort` (`template_id`, `category_sort`, `item_sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评审模板评分项表';

-- changeset system:prj_evaluation_template_data_001
-- comment 初始化评审模板数据
-- 1. TPL_001 产品研发评审模板
INSERT INTO `prj_evaluation_template`
(`id`, `code`, `name`, `description`, `total_score`, `item_count`, `category_count`, `status`, `sort`, `create_user`, `create_time`)
VALUES
(1, 'TPL_001', '产品研发评审模板', '适用于产品研发类项目评审，重点关注产品创新性、市场前景、用户体验和商业价值。', 100.00, 8, 5, 1, 1, 1, NOW());

INSERT INTO `prj_evaluation_template_item`
(`id`, `template_id`, `category_name`, `category_sort`, `item_name`, `item_sort`, `max_score`, `description`, `create_user`, `create_time`)
VALUES
-- 一、项目创新性（20分）
(1, 1, '一、项目创新性', 1, '技术创新程度', 1, 10.00, '评价项目采用的技术是否具有创新性', 1, NOW()),
(2, 1, '一、项目创新性', 1, '应用模式创新', 2, 10.00, '评价项目的应用模式是否新颖', 1, NOW()),

-- 二、技术可行性（25分）
(3, 1, '二、技术可行性', 2, '技术方案合理性', 1, 15.00, '评价技术方案是否合理可行', 1, NOW()),
(4, 1, '二、技术可行性', 2, '实施团队能力', 2, 10.00, '评价团队是否具备实施能力', 1, NOW()),

-- 三、应用价值（30分）
(5, 1, '三、应用价值', 3, '实际应用前景', 1, 15.00, '评价项目的实际应用价值和前景', 1, NOW()),
(6, 1, '三、应用价值', 3, '预期效益', 2, 15.00, '评价项目预期产生的经济和社会效益', 1, NOW()),

-- 四、预算合理性（15分）
(7, 1, '四、预算合理性', 4, '预算编制合理性', 1, 15.00, '评价项目预算是否合理，资金使用是否得当', 1, NOW()),

-- 五、风险控制（10分）
(8, 1, '五、风险控制', 5, '风险识别与应对', 1, 10.00, '评价项目风险识别是否充分，应对措施是否有效', 1, NOW());

-- 2. TPL_002 技术创新评审模板
INSERT INTO `prj_evaluation_template`
(`id`, `code`, `name`, `description`, `total_score`, `item_count`, `category_count`, `status`, `sort`, `create_user`, `create_time`)
VALUES
(2, 'TPL_002', '技术创新评审模板', '适用于技术创新类项目评审，重点关注技术先进性、创新突破性、应用前景和知识产权保护。', 100.00, 10, 5, 1, 2, 1, NOW());

INSERT INTO `prj_evaluation_template_item`
(`id`, `template_id`, `category_name`, `category_sort`, `item_name`, `item_sort`, `max_score`, `description`, `create_user`, `create_time`)
VALUES
-- 一、技术创新性（25分）
(11, 2, '一、技术创新性', 1, '技术先进性', 1, 15.00, '评价项目采用的技术是否处于行业领先水平，是否代表技术发展方向', 1, NOW()),
(12, 2, '一、技术创新性', 1, '创新突破性', 2, 10.00, '评价项目是否有重大技术创新和突破，是否填补行业空白', 1, NOW()),

-- 二、技术可行性（25分）
(13, 2, '二、技术可行性', 2, '技术方案合理性', 1, 15.00, '评价技术路线是否科学合理，实施方案是否切实可行', 1, NOW()),
(14, 2, '二、技术可行性', 2, '实施团队能力', 2, 10.00, '评价项目团队是否具备相应的技术实力和项目经验', 1, NOW()),

-- 三、应用价值（30分）
(15, 2, '三、应用价值', 3, '应用前景', 1, 15.00, '评价技术成果的应用潜力和推广价值', 1, NOW()),
(16, 2, '三、应用价值', 3, '预期效益', 2, 15.00, '评价项目预期产生的经济效益和社会效益', 1, NOW()),

-- 四、预算合理性（10分）
(17, 2, '四、预算合理性', 4, '预算编制合理性', 1, 10.00, '评价项目预算是否合理，资金使用计划是否得当', 1, NOW()),

-- 五、风险控制（10分）
(18, 2, '五、风险控制', 5, '风险识别与应对', 1, 5.00, '评价项目风险识别是否充分，应对措施是否有效', 1, NOW()),
(19, 2, '五、风险控制', 5, '知识产权保护', 2, 3.00, '评价专利布局规划和知识产权保护措施', 1, NOW()),
(20, 2, '五、风险控制', 5, '技术保密措施', 3, 2.00, '评价核心技术保密方案的完善性', 1, NOW());

-- 3. TPL_003 流程优化评审模板
INSERT INTO `prj_evaluation_template`
(`id`, `code`, `name`, `description`, `total_score`, `item_count`, `category_count`, `status`, `sort`, `create_user`, `create_time`)
VALUES
(3, 'TPL_003', '流程优化评审模板', '适用于流程优化类项目评审，重点关注流程改进效果、实施难度、成本效益和推广价值。', 100.00, 6, 4, 1, 3, 1, NOW());

INSERT INTO `prj_evaluation_template_item`
(`id`, `template_id`, `category_name`, `category_sort`, `item_name`, `item_sort`, `max_score`, `description`, `create_user`, `create_time`)
VALUES
-- 一、优化必要性（20分）
(21, 3, '一、优化必要性', 1, '现状问题分析', 1, 10.00, '评价对现有流程问题的分析是否透彻', 1, NOW()),
(22, 3, '一、优化必要性', 1, '优化紧迫性', 2, 10.00, '评价流程优化的紧迫程度和重要性', 1, NOW()),

-- 二、方案可行性（30分）
(23, 3, '二、方案可行性', 2, '优化方案合理性', 1, 20.00, '评价流程优化方案是否科学合理', 1, NOW()),
(24, 3, '二、方案可行性', 2, '实施难度', 2, 10.00, '评价方案实施的难度和可行性', 1, NOW()),

-- 三、预期效益（35分）
(25, 3, '三、预期效益', 3, '效率提升', 1, 20.00, '评价流程优化后的效率提升程度', 1, NOW()),
(26, 3, '三、预期效益', 3, '成本节约', 2, 15.00, '评价流程优化后的成本节约效果', 1, NOW()),

-- 四、推广价值（15分）
(27, 3, '四、推广价值', 4, '推广可行性', 1, 15.00, '评价优化方案在其他部门/业务的推广价值', 1, NOW());

-- 4. TPL_004 基础设施评审模板
INSERT INTO `prj_evaluation_template`
(`id`, `code`, `name`, `description`, `total_score`, `item_count`, `category_count`, `status`, `sort`, `create_user`, `create_time`)
VALUES
(4, 'TPL_004', '基础设施评审模板', '适用于基础设施建设类项目评审，重点关注建设必要性、技术方案、安全可靠性和运维保障。', 100.00, 7, 5, 1, 4, 1, NOW());

INSERT INTO `prj_evaluation_template_item`
(`id`, `template_id`, `category_name`, `category_sort`, `item_name`, `item_sort`, `max_score`, `description`, `create_user`, `create_time`)
VALUES
-- 一、建设必要性（20分）
(31, 4, '一、建设必要性', 1, '需求分析', 1, 10.00, '评价基础设施建设需求分析的充分性', 1, NOW()),
(32, 4, '一、建设必要性', 1, '建设紧迫性', 2, 10.00, '评价基础设施建设的紧迫程度', 1, NOW()),

-- 二、技术方案（25分）
(33, 4, '二、技术方案', 2, '技术先进性', 1, 15.00, '评价采用技术是否先进成熟', 1, NOW()),
(34, 4, '二、技术方案', 2, '方案合理性', 2, 10.00, '评价技术方案是否科学合理', 1, NOW()),

-- 三、安全可靠性（25分）
(35, 4, '三、安全可靠性', 3, '系统可靠性', 1, 15.00, '评价系统稳定性和可靠性保障措施', 1, NOW()),
(36, 4, '三、安全可靠性', 3, '安全防护', 2, 10.00, '评价安全防护措施的完善性', 1, NOW()),

-- 四、运维保障（20分）
(37, 4, '四、运维保障', 4, '运维方案', 1, 20.00, '评价运维保障方案的可行性', 1, NOW()),

-- 五、投资效益（10分）
(38, 4, '五、投资效益', 5, '投资回报', 1, 10.00, '评价基础设施建设的投资效益', 1, NOW());

-- 5. TPL_005 市场拓展评审模板（停用）
INSERT INTO `prj_evaluation_template`
(`id`, `code`, `name`, `description`, `total_score`, `item_count`, `category_count`, `status`, `sort`, `create_user`, `create_time`)
VALUES
(5, 'TPL_005', '市场拓展评审模板', '适用于市场拓展类项目评审，重点关注市场机会、竞争分析、营销策略和预期收益。', 100.00, 9, 5, 2, 5, 1, NOW());

INSERT INTO `prj_evaluation_template_item`
(`id`, `template_id`, `category_name`, `category_sort`, `item_name`, `item_sort`, `max_score`, `description`, `create_user`, `create_time`)
VALUES
-- 一、市场机会（25分）
(41, 5, '一、市场机会', 1, '市场规模', 1, 15.00, '评价目标市场的规模和增长潜力', 1, NOW()),
(42, 5, '一、市场机会', 1, '市场需求', 2, 10.00, '评价市场需求的真实性和持续性', 1, NOW()),

-- 二、竞争分析（20分）
(43, 5, '二、竞争分析', 2, '竞争态势', 1, 10.00, '评价竞争环境和竞争对手分析', 1, NOW()),
(44, 5, '二、竞争分析', 2, '竞争优势', 2, 10.00, '评价自身竞争优势是否明显', 1, NOW()),

-- 三、营销策略（25分）
(45, 5, '三、营销策略', 3, '市场定位', 1, 10.00, '评价市场定位是否清晰准确', 1, NOW()),
(46, 5, '三、营销策略', 3, '营销方案', 2, 10.00, '评价营销策略和执行方案', 1, NOW()),
(47, 5, '三、营销策略', 3, '推广渠道', 3, 5.00, '评价推广渠道的有效性', 1, NOW()),

-- 四、预期收益（20分）
(48, 5, '四、预期收益', 4, '收入预测', 1, 10.00, '评价收入预测的合理性', 1, NOW()),
(49, 5, '四、预期收益', 4, '投资回报', 2, 10.00, '评价投资回报率和回收期', 1, NOW()),

-- 五、风险控制（10分）
(50, 5, '五、风险控制', 5, '市场风险应对', 1, 10.00, '评价市场风险识别和应对措施', 1, NOW());
