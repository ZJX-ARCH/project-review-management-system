-- liquibase formatted sql

-- changeset zjx:form-template-14-data-1
-- comment 初始化14个表单模板数据（7个阶段×2个模板）

-- ==================== 1. 申请表单 (template_type=1) ====================

-- 1.1 科研项目申请表（已存在，ID=1737207001001）
-- 1.2 教学项目申请表
INSERT INTO `review_form_template`
(`id`, `template_name`, `template_code`, `template_type`, `description`, `layout_config`, `status`, `sort`, `create_user`, `create_time`, `deleted`)
VALUES
(1737207001002, '教学项目申请表', 'FORM_TEACHING_APP', 1, '适用于教学改革项目申请的标准表单模板', '{"gridCols":24,"labelWidth":120,"labelAlign":"right"}', 1, 2, 1, NOW(), 0);

INSERT INTO `review_form_field`
(`id`, `template_id`, `field_name`, `field_code`, `field_type`, `span`, `is_required`, `sort`, `field_config`, `create_user`, `create_time`, `deleted`)
VALUES
(1737207101101, 1737207001002, '项目名称', 'projectName', 'TEXT', 12, b'1', 1, '{"placeholder":"请输入教学项目名称","maxLength":100}', 1, NOW(), 0),
(1737207101102, 1737207001002, '课程类别', 'courseCategory', 'SELECT', 8, b'1', 2, '{"placeholder":"请选择课程类别","options":[{"label":"理论课","value":"THEORY"},{"label":"实验课","value":"LAB"},{"label":"实践课","value":"PRACTICE"}],"allowClear":true}', 1, NOW(), 0),
(1737207101103, 1737207001002, '申请日期', 'applicationDate', 'DATE', 8, b'1', 3, '{"placeholder":"请选择申请日期","format":"YYYY-MM-DD"}', 1, NOW(), 0),
(1737207101104, 1737207001002, '教学目标', 'teachingGoals', 'TEXTAREA', 24, b'1', 4, '{"placeholder":"请详细描述教学改革目标","rows":4,"maxLength":1000,"showWordCount":true}', 1, NOW(), 0),
(1737207101105, 1737207001002, '改革方案', 'reformPlan', 'TEXTAREA', 24, b'1', 5, '{"placeholder":"请详细描述教学改革方案","rows":6,"maxLength":2000,"showWordCount":true}', 1, NOW(), 0),
(1737207101106, 1737207001002, '预期成果', 'expectedResults', 'TEXTAREA', 24, b'1', 6, '{"placeholder":"请描述预期教学成果","rows":4,"maxLength":1000,"showWordCount":true}', 1, NOW(), 0),
(1737207101107, 1737207001002, '支撑材料', 'attachments', 'FILE', 24, b'0', 7, '{"maxCount":5,"maxSize":20,"allowedTypes":[".pdf",".docx",".pptx"],"tips":"支持PDF、Word、PPT格式，单文件最大20MB"}', 1, NOW(), 0);


-- ==================== 2. 审核表单 (template_type=2) ====================

-- 2.1 初审意见表
INSERT INTO `review_form_template`
(`id`, `template_name`, `template_code`, `template_type`, `description`, `layout_config`, `status`, `sort`, `create_user`, `create_time`, `deleted`)
VALUES
(1737207002001, '初审意见表', 'FORM_FIRST_AUDIT', 2, '适用于项目初审的意见填写表单', '{"gridCols":24,"labelWidth":120,"labelAlign":"right"}', 1, 1, 1, NOW(), 0);

INSERT INTO `review_form_field`
(`id`, `template_id`, `field_name`, `field_code`, `field_type`, `span`, `is_required`, `sort`, `field_config`, `create_user`, `create_time`, `deleted`)
VALUES
(1737207102001, 1737207002001, '审核结论', 'auditResult', 'RADIO', 12, b'1', 1, '{"options":[{"label":"通过","value":"PASS"},{"label":"不通过","value":"REJECT"},{"label":"需补充材料","value":"SUPPLEMENT"}]}', 1, NOW(), 0),
(1737207102002, 1737207002001, '审核日期', 'auditDate', 'DATE', 8, b'1', 2, '{"placeholder":"请选择审核日期","format":"YYYY-MM-DD"}', 1, NOW(), 0),
(1737207102003, 1737207002001, '项目创新性', 'innovation', 'SCORE', 8, b'1', 3, '{"count":5,"allowHalf":true}', 1, NOW(), 0),
(1737207102004, 1737207002001, '项目可行性', 'feasibility', 'SCORE', 8, b'1', 4, '{"count":5,"allowHalf":true}', 1, NOW(), 0),
(1737207102005, 1737207002001, '预算合理性', 'budgetRationality', 'SCORE', 8, b'1', 5, '{"count":5,"allowHalf":true}', 1, NOW(), 0),
(1737207102006, 1737207002001, '审核意见', 'auditOpinion', 'TEXTAREA', 24, b'1', 6, '{"placeholder":"请填写详细的审核意见","rows":6,"maxLength":2000,"showWordCount":true}', 1, NOW(), 0),
(1737207102007, 1737207002001, '改进建议', 'suggestions', 'TEXTAREA', 24, b'0', 7, '{"placeholder":"请提出改进建议","rows":4,"maxLength":1000,"showWordCount":true}', 1, NOW(), 0);

-- 2.2 复审意见表
INSERT INTO `review_form_template`
(`id`, `template_name`, `template_code`, `template_type`, `description`, `layout_config`, `status`, `sort`, `create_user`, `create_time`, `deleted`)
VALUES
(1737207002002, '复审意见表', 'FORM_SECOND_AUDIT', 2, '适用于项目复审的意见填写表单', '{"gridCols":24,"labelWidth":120,"labelAlign":"right"}', 1, 2, 1, NOW(), 0);

INSERT INTO `review_form_field`
(`id`, `template_id`, `field_name`, `field_code`, `field_type`, `span`, `is_required`, `sort`, `field_config`, `create_user`, `create_time`, `deleted`)
VALUES
(1737207102101, 1737207002002, '复审结论', 'auditResult', 'RADIO', 12, b'1', 1, '{"options":[{"label":"通过","value":"PASS"},{"label":"不通过","value":"REJECT"},{"label":"需进一步审核","value":"FURTHER"}]}', 1, NOW(), 0),
(1737207102102, 1737207002002, '复审日期', 'auditDate', 'DATE', 8, b'1', 2, '{"placeholder":"请选择复审日期","format":"YYYY-MM-DD"}', 1, NOW(), 0),
(1737207102103, 1737207002002, '初审意见落实情况', 'implementationStatus', 'RADIO', 12, b'1', 3, '{"options":[{"label":"已完全落实","value":"FULL"},{"label":"部分落实","value":"PARTIAL"},{"label":"未落实","value":"NONE"}]}', 1, NOW(), 0),
(1737207102104, 1737207002002, '补充材料完整性', 'completeness', 'SCORE', 12, b'1', 4, '{"count":5,"allowHalf":true}', 1, NOW(), 0),
(1737207102105, 1737207002002, '复审意见', 'auditOpinion', 'TEXTAREA', 24, b'1', 5, '{"placeholder":"请填写详细的复审意见","rows":6,"maxLength":2000,"showWordCount":true}', 1, NOW(), 0),
(1737207102106, 1737207002002, '后续要求', 'requirements', 'TEXTAREA', 24, b'0', 6, '{"placeholder":"请说明后续要求","rows":4,"maxLength":1000,"showWordCount":true}', 1, NOW(), 0);


-- ==================== 3. 评审表单 (template_type=3) ====================

-- 3.1 专家评审表
INSERT INTO `review_form_template`
(`id`, `template_name`, `template_code`, `template_type`, `description`, `layout_config`, `status`, `sort`, `create_user`, `create_time`, `deleted`)
VALUES
(1737207003001, '专家评审表', 'FORM_EXPERT_REVIEW', 3, '适用于专家评审项目的评分表单', '{"gridCols":24,"labelWidth":120,"labelAlign":"right"}', 1, 1, 1, NOW(), 0);

INSERT INTO `review_form_field`
(`id`, `template_id`, `field_name`, `field_code`, `field_type`, `span`, `is_required`, `sort`, `field_config`, `create_user`, `create_time`, `deleted`)
VALUES
(1737207103001, 1737207003001, '评审结论', 'reviewResult', 'RADIO', 12, b'1', 1, '{"options":[{"label":"优秀","value":"EXCELLENT"},{"label":"良好","value":"GOOD"},{"label":"合格","value":"QUALIFIED"},{"label":"不合格","value":"UNQUALIFIED"}]}', 1, NOW(), 0),
(1737207103002, 1737207003001, '评审日期', 'reviewDate', 'DATE', 8, b'1', 2, '{"placeholder":"请选择评审日期","format":"YYYY-MM-DD"}', 1, NOW(), 0),
(1737207103003, 1737207003001, '评分明细', 'scoreDetails', 'TABLE', 24, b'1', 3, '{"columns":[{"name":"评分项","code":"item","type":"TEXT","width":200,"required":true},{"name":"权重(%)","code":"weight","type":"NUMBER","width":100,"required":true,"precision":0},{"name":"得分","code":"score","type":"NUMBER","width":100,"required":true,"precision":1},{"name":"说明","code":"remark","type":"TEXT","width":300,"required":false}],"minRows":1,"maxRows":10}', 1, NOW(), 0),
(1737207103004, 1737207003001, '创新性评价', 'innovationReview', 'TEXTAREA', 24, b'1', 4, '{"placeholder":"请评价项目的创新性","rows":4,"maxLength":1000,"showWordCount":true}', 1, NOW(), 0),
(1737207103005, 1737207003001, '可行性评价', 'feasibilityReview', 'TEXTAREA', 24, b'1', 5, '{"placeholder":"请评价项目的可行性","rows":4,"maxLength":1000,"showWordCount":true}', 1, NOW(), 0),
(1737207103006, 1737207003001, '综合评价', 'comprehensiveReview', 'TEXTAREA', 24, b'1', 6, '{"placeholder":"请给出综合评价意见","rows":6,"maxLength":2000,"showWordCount":true}', 1, NOW(), 0);

-- 3.2 评审汇总表
INSERT INTO `review_form_template`
(`id`, `template_name`, `template_code`, `template_type`, `description`, `layout_config`, `status`, `sort`, `create_user`, `create_time`, `deleted`)
VALUES
(1737207003002, '评审汇总表', 'FORM_REVIEW_SUMMARY', 3, '适用于汇总多位专家评审意见的表单', '{"gridCols":24,"labelWidth":120,"labelAlign":"right"}', 1, 2, 1, NOW(), 0);

INSERT INTO `review_form_field`
(`id`, `template_id`, `field_name`, `field_code`, `field_type`, `span`, `is_required`, `sort`, `field_config`, `create_user`, `create_time`, `deleted`)
VALUES
(1737207103101, 1737207003002, '汇总日期', 'summaryDate', 'DATE', 8, b'1', 1, '{"placeholder":"请选择汇总日期","format":"YYYY-MM-DD"}', 1, NOW(), 0),
(1737207103102, 1737207003002, '参评专家数', 'expertCount', 'NUMBER', 8, b'1', 2, '{"placeholder":"请输入参评专家数","min":1,"max":100,"precision":0}', 1, NOW(), 0),
(1737207103103, 1737207003002, '平均得分', 'averageScore', 'NUMBER', 8, b'1', 3, '{"placeholder":"自动计算平均分","precision":2}', 1, NOW(), 0),
(1737207103104, 1737207003002, '专家意见汇总', 'expertOpinionsSummary', 'TABLE', 24, b'1', 4, '{"columns":[{"name":"专家姓名","code":"expertName","type":"TEXT","width":120,"required":true},{"name":"评审结论","code":"conclusion","type":"SELECT","width":120,"required":true,"options":[{"label":"优秀","value":"EXCELLENT"},{"label":"良好","value":"GOOD"},{"label":"合格","value":"QUALIFIED"},{"label":"不合格","value":"UNQUALIFIED"}]},{"name":"得分","code":"score","type":"NUMBER","width":100,"required":true,"precision":2},{"name":"主要意见","code":"opinion","type":"TEXT","width":300,"required":false}],"minRows":1}', 1, NOW(), 0),
(1737207103105, 1737207003002, '汇总意见', 'summaryOpinion', 'TEXTAREA', 24, b'1', 5, '{"placeholder":"请填写汇总意见","rows":6,"maxLength":2000,"showWordCount":true}', 1, NOW(), 0),
(1737207103106, 1737207003002, '建议', 'recommendation', 'TEXTAREA', 24, b'0', 6, '{"placeholder":"请提出建议","rows":4,"maxLength":1000,"showWordCount":true}', 1, NOW(), 0);


-- ==================== 4. 决策表单 (template_type=4) ====================

-- 4.1 立项决策表
INSERT INTO `review_form_template`
(`id`, `template_name`, `template_code`, `template_type`, `description`, `layout_config`, `status`, `sort`, `create_user`, `create_time`, `deleted`)
VALUES
(1737207004001, '立项决策表', 'FORM_APPROVAL_DEC', 4, '适用于项目立项决策的表单', '{"gridCols":24,"labelWidth":120,"labelAlign":"right"}', 1, 1, 1, NOW(), 0);

INSERT INTO `review_form_field`
(`id`, `template_id`, `field_name`, `field_code`, `field_type`, `span`, `is_required`, `sort`, `field_config`, `create_user`, `create_time`, `deleted`)
VALUES
(1737207104001, 1737207004001, '决策结果', 'decisionResult', 'RADIO', 12, b'1', 1, '{"options":[{"label":"批准立项","value":"APPROVE"},{"label":"暂缓立项","value":"DEFER"},{"label":"不予立项","value":"REJECT"}]}', 1, NOW(), 0),
(1737207104002, 1737207004001, '决策日期', 'decisionDate', 'DATE', 8, b'1', 2, '{"placeholder":"请选择决策日期","format":"YYYY-MM-DD"}', 1, NOW(), 0),
(1737207104003, 1737207004001, '决策优先级', 'priority', 'SELECT', 8, b'1', 3, '{"placeholder":"请选择优先级","options":[{"label":"高","value":"HIGH"},{"label":"中","value":"MEDIUM"},{"label":"低","value":"LOW"}],"allowClear":true}', 1, NOW(), 0),
(1737207104004, 1737207004001, '立项条件', 'approvalConditions', 'CHECKBOX', 24, b'0', 4, '{"options":[{"label":"通过全部审核","value":"AUDIT_PASSED"},{"label":"专家评审优秀","value":"REVIEW_EXCELLENT"},{"label":"预算审批通过","value":"BUDGET_APPROVED"},{"label":"符合战略规划","value":"STRATEGIC_ALIGNED"}]}', 1, NOW(), 0),
(1737207104005, 1737207004001, '决策依据', 'decisionBasis', 'TEXTAREA', 24, b'1', 5, '{"placeholder":"请说明决策依据","rows":6,"maxLength":2000,"showWordCount":true}', 1, NOW(), 0),
(1737207104006, 1737207004001, '特殊要求', 'specialRequirements', 'TEXTAREA', 24, b'0', 6, '{"placeholder":"请说明特殊要求和注意事项","rows":4,"maxLength":1000,"showWordCount":true}', 1, NOW(), 0);

-- 4.2 资助决策表
INSERT INTO `review_form_template`
(`id`, `template_name`, `template_code`, `template_type`, `description`, `layout_config`, `status`, `sort`, `create_user`, `create_time`, `deleted`)
VALUES
(1737207004002, '资助决策表', 'FORM_FUNDING_DEC', 4, '适用于项目资助决策的表单', '{"gridCols":24,"labelWidth":120,"labelAlign":"right"}', 1, 2, 1, NOW(), 0);

INSERT INTO `review_form_field`
(`id`, `template_id`, `field_name`, `field_code`, `field_type`, `span`, `is_required`, `sort`, `field_config`, `create_user`, `create_time`, `deleted`)
VALUES
(1737207104101, 1737207004002, '资助决定', 'fundingDecision', 'RADIO', 12, b'1', 1, '{"options":[{"label":"全额资助","value":"FULL"},{"label":"部分资助","value":"PARTIAL"},{"label":"不予资助","value":"NONE"}]}', 1, NOW(), 0),
(1737207104102, 1737207004002, '决策日期', 'decisionDate', 'DATE', 8, b'1', 2, '{"placeholder":"请选择决策日期","format":"YYYY-MM-DD"}', 1, NOW(), 0),
(1737207104103, 1737207004002, '资助金额（万元）', 'fundingAmount', 'NUMBER', 8, b'1', 3, '{"placeholder":"请输入资助金额","min":0,"max":10000,"precision":2}', 1, NOW(), 0),
(1737207104104, 1737207004002, '资助周期（年）', 'fundingPeriod', 'NUMBER', 8, b'1', 4, '{"placeholder":"请输入资助周期","min":1,"max":10,"precision":0}', 1, NOW(), 0),
(1737207104105, 1737207004002, '资助分配方案', 'allocationPlan', 'TABLE', 24, b'0', 5, '{"columns":[{"name":"年度","code":"year","type":"NUMBER","width":100,"required":true,"precision":0},{"name":"金额（万元）","code":"amount","type":"NUMBER","width":150,"required":true,"precision":2},{"name":"用途说明","code":"purpose","type":"TEXT","width":300,"required":false}],"minRows":1}', 1, NOW(), 0),
(1737207104106, 1737207004002, '资助说明', 'fundingDescription', 'TEXTAREA', 24, b'1', 6, '{"placeholder":"请说明资助决策理由","rows":6,"maxLength":2000,"showWordCount":true}', 1, NOW(), 0),
(1737207104107, 1737207004002, '使用要求', 'usageRequirements', 'TEXTAREA', 24, b'0', 7, '{"placeholder":"请说明资金使用要求","rows":4,"maxLength":1000,"showWordCount":true}', 1, NOW(), 0);


-- ==================== 5. 立项阶段 (template_type=5) ====================

-- 5.1 立项申请表
INSERT INTO `review_form_template`
(`id`, `template_name`, `template_code`, `template_type`, `description`, `layout_config`, `status`, `sort`, `create_user`, `create_time`, `deleted`)
VALUES
(1737207005001, '立项申请表', 'FORM_KICKOFF_APP', 5, '适用于项目立项阶段的申请表单', '{"gridCols":24,"labelWidth":120,"labelAlign":"right"}', 1, 1, 1, NOW(), 0);

INSERT INTO `review_form_field`
(`id`, `template_id`, `field_name`, `field_code`, `field_type`, `span`, `is_required`, `sort`, `field_config`, `create_user`, `create_time`, `deleted`)
VALUES
(1737207105001, 1737207005001, '项目名称', 'projectName', 'TEXT', 12, b'1', 1, '{"placeholder":"请输入项目名称","maxLength":100}', 1, NOW(), 0),
(1737207105002, 1737207005001, '项目负责人', 'projectLeader', 'TEXT', 8, b'1', 2, '{"placeholder":"请输入项目负责人","maxLength":50}', 1, NOW(), 0),
(1737207105003, 1737207005001, '立项日期', 'kickoffDate', 'DATE', 8, b'1', 3, '{"placeholder":"请选择立项日期","format":"YYYY-MM-DD"}', 1, NOW(), 0),
(1737207105004, 1737207005001, '项目目标', 'objectives', 'TEXTAREA', 24, b'1', 4, '{"placeholder":"请详细描述项目目标","rows":5,"maxLength":1500,"showWordCount":true}', 1, NOW(), 0),
(1737207105005, 1737207005001, '实施方案', 'implementationPlan', 'TEXTAREA', 24, b'1', 5, '{"placeholder":"请详细描述实施方案","rows":6,"maxLength":2000,"showWordCount":true}', 1, NOW(), 0),
(1737207105006, 1737207005001, '团队成员', 'teamMembers', 'TABLE', 24, b'1', 6, '{"columns":[{"name":"姓名","code":"name","type":"TEXT","width":120,"required":true},{"name":"职称","code":"title","type":"TEXT","width":120,"required":true},{"name":"职责","code":"responsibility","type":"TEXT","width":150,"required":true},{"name":"工作量(%)","code":"workload","type":"NUMBER","width":100,"required":true,"precision":0}],"minRows":1}', 1, NOW(), 0),
(1737207105007, 1737207005001, '立项附件', 'attachments', 'FILE', 24, b'0', 7, '{"maxCount":5,"maxSize":20,"allowedTypes":[".pdf",".docx"],"tips":"支持PDF、Word格式，单文件最大20MB"}', 1, NOW(), 0);

-- 5.2 立项可行性分析表
INSERT INTO `review_form_template`
(`id`, `template_name`, `template_code`, `template_type`, `description`, `layout_config`, `status`, `sort`, `create_user`, `create_time`, `deleted`)
VALUES
(1737207005002, '立项可行性分析表', 'FORM_FEASIBILITY', 5, '适用于项目立项可行性分析的表单', '{"gridCols":24,"labelWidth":120,"labelAlign":"right"}', 1, 2, 1, NOW(), 0);

INSERT INTO `review_form_field`
(`id`, `template_id`, `field_name`, `field_code`, `field_type`, `span`, `is_required`, `sort`, `field_config`, `create_user`, `create_time`, `deleted`)
VALUES
(1737207105101, 1737207005002, '分析日期', 'analysisDate', 'DATE', 8, b'1', 1, '{"placeholder":"请选择分析日期","format":"YYYY-MM-DD"}', 1, NOW(), 0),
(1737207105102, 1737207005002, '技术可行性', 'technicalFeasibility', 'RADIO', 12, b'1', 2, '{"options":[{"label":"可行","value":"FEASIBLE"},{"label":"部分可行","value":"PARTIAL"},{"label":"不可行","value":"INFEASIBLE"}]}', 1, NOW(), 0),
(1737207105103, 1737207005002, '经济可行性', 'economicFeasibility', 'RADIO', 12, b'1', 3, '{"options":[{"label":"可行","value":"FEASIBLE"},{"label":"部分可行","value":"PARTIAL"},{"label":"不可行","value":"INFEASIBLE"}]}', 1, NOW(), 0),
(1737207105104, 1737207005002, '技术分析', 'technicalAnalysis', 'TEXTAREA', 24, b'1', 4, '{"placeholder":"请进行技术可行性分析","rows":5,"maxLength":1500,"showWordCount":true}', 1, NOW(), 0),
(1737207105105, 1737207005002, '经济分析', 'economicAnalysis', 'TEXTAREA', 24, b'1', 5, '{"placeholder":"请进行经济可行性分析","rows":5,"maxLength":1500,"showWordCount":true}', 1, NOW(), 0),
(1737207105106, 1737207005002, '风险评估', 'riskAssessment', 'TABLE', 24, b'1', 6, '{"columns":[{"name":"风险类型","code":"riskType","type":"TEXT","width":150,"required":true},{"name":"风险等级","code":"riskLevel","type":"SELECT","width":120,"required":true,"options":[{"label":"高","value":"HIGH"},{"label":"中","value":"MEDIUM"},{"label":"低","value":"LOW"}]},{"name":"应对措施","code":"mitigation","type":"TEXT","width":300,"required":true}],"minRows":1}', 1, NOW(), 0),
(1737207105107, 1737207005002, '综合结论', 'conclusion', 'TEXTAREA', 24, b'1', 7, '{"placeholder":"请给出综合可行性结论","rows":4,"maxLength":1000,"showWordCount":true}', 1, NOW(), 0);


-- ==================== 6. 执行阶段 (template_type=6) ====================

-- 6.1 项目进度汇报表
INSERT INTO `review_form_template`
(`id`, `template_name`, `template_code`, `template_type`, `description`, `layout_config`, `status`, `sort`, `create_user`, `create_time`, `deleted`)
VALUES
(1737207006001, '项目进度汇报表', 'FORM_PROGRESS_REPORT', 6, '适用于项目执行阶段的进度汇报表单', '{"gridCols":24,"labelWidth":120,"labelAlign":"right"}', 1, 1, 1, NOW(), 0);

INSERT INTO `review_form_field`
(`id`, `template_id`, `field_name`, `field_code`, `field_type`, `span`, `is_required`, `sort`, `field_config`, `create_user`, `create_time`, `deleted`)
VALUES
(1737207106001, 1737207006001, '汇报日期', 'reportDate', 'DATE', 8, b'1', 1, '{"placeholder":"请选择汇报日期","format":"YYYY-MM-DD"}', 1, NOW(), 0),
(1737207106002, 1737207006001, '汇报周期', 'reportPeriod', 'SELECT', 8, b'1', 2, '{"placeholder":"请选择汇报周期","options":[{"label":"月度","value":"MONTHLY"},{"label":"季度","value":"QUARTERLY"},{"label":"半年度","value":"HALF_YEARLY"}],"allowClear":true}', 1, NOW(), 0),
(1737207106003, 1737207006001, '总体进度(%)', 'overallProgress', 'NUMBER', 8, b'1', 3, '{"placeholder":"请输入总体进度","min":0,"max":100,"precision":0}', 1, NOW(), 0),
(1737207106004, 1737207006001, '完成情况', 'completionStatus', 'TEXTAREA', 24, b'1', 4, '{"placeholder":"请描述本期完成情况","rows":5,"maxLength":1500,"showWordCount":true}', 1, NOW(), 0),
(1737207106005, 1737207006001, '任务进度明细', 'taskProgress', 'TABLE', 24, b'1', 5, '{"columns":[{"name":"任务名称","code":"taskName","type":"TEXT","width":200,"required":true},{"name":"计划进度(%)","code":"plannedProgress","type":"NUMBER","width":120,"required":true,"precision":0},{"name":"实际进度(%)","code":"actualProgress","type":"NUMBER","width":120,"required":true,"precision":0},{"name":"状态","code":"status","type":"SELECT","width":120,"required":true,"options":[{"label":"正常","value":"NORMAL"},{"label":"延期","value":"DELAYED"},{"label":"完成","value":"COMPLETED"}]}],"minRows":1}', 1, NOW(), 0),
(1737207106006, 1737207006001, '遇到的问题', 'problems', 'TEXTAREA', 24, b'0', 6, '{"placeholder":"请描述遇到的问题","rows":4,"maxLength":1000,"showWordCount":true}', 1, NOW(), 0),
(1737207106007, 1737207006001, '下期计划', 'nextPlan', 'TEXTAREA', 24, b'1', 7, '{"placeholder":"请说明下期工作计划","rows":4,"maxLength":1000,"showWordCount":true}', 1, NOW(), 0);

-- 6.2 中期检查表
INSERT INTO `review_form_template`
(`id`, `template_name`, `template_code`, `template_type`, `description`, `layout_config`, `status`, `sort`, `create_user`, `create_time`, `deleted`)
VALUES
(1737207006002, '中期检查表', 'FORM_MIDTERM_CHECK', 6, '适用于项目执行阶段的中期检查表单', '{"gridCols":24,"labelWidth":120,"labelAlign":"right"}', 1, 2, 1, NOW(), 0);

INSERT INTO `review_form_field`
(`id`, `template_id`, `field_name`, `field_code`, `field_type`, `span`, `is_required`, `sort`, `field_config`, `create_user`, `create_time`, `deleted`)
VALUES
(1737207106101, 1737207006002, '检查日期', 'checkDate', 'DATE', 8, b'1', 1, '{"placeholder":"请选择检查日期","format":"YYYY-MM-DD"}', 1, NOW(), 0),
(1737207106102, 1737207006002, '检查结论', 'checkResult', 'RADIO', 12, b'1', 2, '{"options":[{"label":"优秀","value":"EXCELLENT"},{"label":"良好","value":"GOOD"},{"label":"合格","value":"QUALIFIED"},{"label":"需整改","value":"RECTIFICATION"}]}', 1, NOW(), 0),
(1737207106103, 1737207006002, '进度评价', 'progressEvaluation', 'SCORE', 8, b'1', 3, '{"count":5,"allowHalf":true}', 1, NOW(), 0),
(1737207106104, 1737207006002, '质量评价', 'qualityEvaluation', 'SCORE', 8, b'1', 4, '{"count":5,"allowHalf":true}', 1, NOW(), 0),
(1737207106105, 1737207006002, '经费使用评价', 'budgetEvaluation', 'SCORE', 8, b'1', 5, '{"count":5,"allowHalf":true}', 1, NOW(), 0),
(1737207106106, 1737207006002, '检查指标', 'checkIndicators', 'TABLE', 24, b'1', 6, '{"columns":[{"name":"检查指标","code":"indicator","type":"TEXT","width":200,"required":true},{"name":"目标值","code":"target","type":"TEXT","width":150,"required":true},{"name":"实际值","code":"actual","type":"TEXT","width":150,"required":true},{"name":"评价","code":"evaluation","type":"SELECT","width":120,"required":true,"options":[{"label":"达标","value":"MET"},{"label":"未达标","value":"NOT_MET"}]}],"minRows":1}', 1, NOW(), 0),
(1737207106107, 1737207006002, '检查意见', 'checkOpinion', 'TEXTAREA', 24, b'1', 7, '{"placeholder":"请填写检查意见","rows":6,"maxLength":2000,"showWordCount":true}', 1, NOW(), 0),
(1737207106108, 1737207006002, '整改要求', 'rectificationRequirements', 'TEXTAREA', 24, b'0', 8, '{"placeholder":"请说明整改要求","rows":4,"maxLength":1000,"showWordCount":true}', 1, NOW(), 0);


-- ==================== 7. 验收阶段 (template_type=7) ====================

-- 7.1 结项验收表
INSERT INTO `review_form_template`
(`id`, `template_name`, `template_code`, `template_type`, `description`, `layout_config`, `status`, `sort`, `create_user`, `create_time`, `deleted`)
VALUES
(1737207007001, '结项验收表', 'FORM_ACCEPTANCE', 7, '适用于项目验收阶段的结项验收表单', '{"gridCols":24,"labelWidth":120,"labelAlign":"right"}', 1, 1, 1, NOW(), 0);

INSERT INTO `review_form_field`
(`id`, `template_id`, `field_name`, `field_code`, `field_type`, `span`, `is_required`, `sort`, `field_config`, `create_user`, `create_time`, `deleted`)
VALUES
(1737207107001, 1737207007001, '验收日期', 'acceptanceDate', 'DATE', 8, b'1', 1, '{"placeholder":"请选择验收日期","format":"YYYY-MM-DD"}', 1, NOW(), 0),
(1737207107002, 1737207007001, '验收结论', 'acceptanceResult', 'RADIO', 12, b'1', 2, '{"options":[{"label":"通过验收","value":"PASS"},{"label":"有条件通过","value":"CONDITIONAL_PASS"},{"label":"不通过","value":"FAIL"}]}', 1, NOW(), 0),
(1737207107003, 1737207007001, '项目完成度(%)', 'completionRate', 'NUMBER', 8, b'1', 3, '{"placeholder":"请输入项目完成度","min":0,"max":100,"precision":0}', 1, NOW(), 0),
(1737207107004, 1737207007001, '目标达成情况', 'objectiveAchievement', 'TEXTAREA', 24, b'1', 4, '{"placeholder":"请描述目标达成情况","rows":5,"maxLength":1500,"showWordCount":true}', 1, NOW(), 0),
(1737207107005, 1737207007001, '成果清单', 'achievementList', 'TABLE', 24, b'1', 5, '{"columns":[{"name":"成果名称","code":"name","type":"TEXT","width":200,"required":true},{"name":"成果类型","code":"type","type":"SELECT","width":150,"required":true,"options":[{"label":"论文","value":"PAPER"},{"label":"专利","value":"PATENT"},{"label":"软著","value":"SOFTWARE"},{"label":"其他","value":"OTHER"}]},{"name":"数量","code":"quantity","type":"NUMBER","width":100,"required":true,"precision":0},{"name":"说明","code":"description","type":"TEXT","width":250,"required":false}],"minRows":1}', 1, NOW(), 0),
(1737207107006, 1737207007001, '经费使用情况', 'budgetUsage', 'TEXTAREA', 24, b'1', 6, '{"placeholder":"请说明经费使用情况","rows":4,"maxLength":1000,"showWordCount":true}', 1, NOW(), 0),
(1737207107007, 1737207007001, '验收意见', 'acceptanceOpinion', 'TEXTAREA', 24, b'1', 7, '{"placeholder":"请填写验收意见","rows":6,"maxLength":2000,"showWordCount":true}', 1, NOW(), 0),
(1737207107008, 1737207007001, '验收附件', 'attachments', 'FILE', 24, b'0', 8, '{"maxCount":10,"maxSize":50,"allowedTypes":[".pdf",".docx",".xlsx"],"tips":"支持PDF、Word、Excel格式，单文件最大50MB"}', 1, NOW(), 0);

-- 7.2 成果评价表
INSERT INTO `review_form_template`
(`id`, `template_name`, `template_code`, `template_type`, `description`, `layout_config`, `status`, `sort`, `create_user`, `create_time`, `deleted`)
VALUES
(1737207007002, '成果评价表', 'FORM_ACHIEVE_EVAL', 7, '适用于项目验收阶段的成果评价表单', '{"gridCols":24,"labelWidth":120,"labelAlign":"right"}', 1, 2, 1, NOW(), 0);

INSERT INTO `review_form_field`
(`id`, `template_id`, `field_name`, `field_code`, `field_type`, `span`, `is_required`, `sort`, `field_config`, `create_user`, `create_time`, `deleted`)
VALUES
(1737207107101, 1737207007002, '评价日期', 'evaluationDate', 'DATE', 8, b'1', 1, '{"placeholder":"请选择评价日期","format":"YYYY-MM-DD"}', 1, NOW(), 0),
(1737207107102, 1737207007002, '成果等级', 'achievementLevel', 'RADIO', 12, b'1', 2, '{"options":[{"label":"优秀","value":"EXCELLENT"},{"label":"良好","value":"GOOD"},{"label":"合格","value":"QUALIFIED"},{"label":"不合格","value":"UNQUALIFIED"}]}', 1, NOW(), 0),
(1737207107103, 1737207007002, '学术价值', 'academicValue', 'SCORE', 8, b'1', 3, '{"count":5,"allowHalf":true}', 1, NOW(), 0),
(1737207107104, 1737207007002, '应用价值', 'applicationValue', 'SCORE', 8, b'1', 4, '{"count":5,"allowHalf":true}', 1, NOW(), 0),
(1737207107105, 1737207007002, '创新性', 'innovation', 'SCORE', 8, b'1', 5, '{"count":5,"allowHalf":true}', 1, NOW(), 0),
(1737207107106, 1737207007002, '成果评价明细', 'achievementDetails', 'TABLE', 24, b'1', 6, '{"columns":[{"name":"评价维度","code":"dimension","type":"TEXT","width":150,"required":true},{"name":"权重(%)","code":"weight","type":"NUMBER","width":100,"required":true,"precision":0},{"name":"得分","code":"score","type":"NUMBER","width":100,"required":true,"precision":1},{"name":"评价说明","code":"comment","type":"TEXT","width":300,"required":false}],"minRows":1}', 1, NOW(), 0),
(1737207107107, 1737207007002, '成果亮点', 'highlights', 'TEXTAREA', 24, b'1', 7, '{"placeholder":"请描述成果的主要亮点","rows":5,"maxLength":1500,"showWordCount":true}', 1, NOW(), 0),
(1737207107108, 1737207007002, '推广建议', 'promotionSuggestions', 'TEXTAREA', 24, b'0', 8, '{"placeholder":"请提出成果推广建议","rows":4,"maxLength":1000,"showWordCount":true}', 1, NOW(), 0),
(1737207107109, 1737207007002, '综合评价', 'comprehensiveEvaluation', 'TEXTAREA', 24, b'1', 9, '{"placeholder":"请给出综合评价","rows":6,"maxLength":2000,"showWordCount":true}', 1, NOW(), 0);
