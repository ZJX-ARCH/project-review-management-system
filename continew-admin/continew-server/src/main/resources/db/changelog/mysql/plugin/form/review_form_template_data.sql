-- liquibase formatted sql

-- changeset zjx:form-template-data-1
-- comment 初始化表单模板示例数据

-- 初始化示例表单模板：科研项目申请表
INSERT INTO `review_form_template`
(`id`, `template_name`, `template_code`, `template_type`, `description`, `layout_config`, `status`, `sort`, `create_user`, `create_time`, `deleted`)
VALUES
(1737207001001, '科研项目申请表', 'FORM_RESEARCH_APPLICATION', 1, '适用于科研项目申请的标准表单模板', '{"gridCols":24,"labelWidth":120,"labelAlign":"right"}', 1, 1, 1, NOW(), 0);

-- 初始化示例字段配置
INSERT INTO `review_form_field`
(`id`, `template_id`, `field_name`, `field_code`, `field_type`, `span`, `is_required`, `sort`, `field_config`, `create_user`, `create_time`, `deleted`)
VALUES
-- 项目基本信息
(1737207101001, 1737207001001, '项目名称', 'projectName', 'TEXT', 12, b'1', 1, '{"placeholder":"请输入项目名称","maxLength":100}', 1, NOW(), 0),
(1737207101002, 1737207001001, '项目类型', 'projectType', 'SELECT', 8, b'1', 2, '{"placeholder":"请选择项目类型","options":[{"label":"基础研究","value":"BASIC"},{"label":"应用研究","value":"APPLIED"},{"label":"开发研究","value":"DEVELOPMENT"}],"allowClear":true}', 1, NOW(), 0),
(1737207101003, 1737207001001, '申请日期', 'applicationDate', 'DATE', 8, b'1', 3, '{"placeholder":"请选择申请日期","format":"YYYY-MM-DD"}', 1, NOW(), 0),

-- 项目详细描述
(1737207101004, 1737207001001, '项目背景', 'projectBackground', 'TEXTAREA', 24, b'1', 4, '{"placeholder":"请详细描述项目研究背景","rows":5,"maxLength":2000,"showWordCount":true}', 1, NOW(), 0),
(1737207101005, 1737207001001, '研究内容', 'researchContent', 'TEXTAREA', 24, b'1', 5, '{"placeholder":"请详细描述研究内容和方法","rows":5,"maxLength":2000,"showWordCount":true}', 1, NOW(), 0),

-- 预算明细（动态表格）
(1737207101006, 1737207001001, '预算明细', 'budgetDetails', 'TABLE', 24, b'1', 6, '{"columns":[{"name":"预算科目","code":"subject","type":"TEXT","width":200,"required":true},{"name":"金额（万元）","code":"amount","type":"NUMBER","width":120,"required":true,"precision":2},{"name":"说明","code":"remark","type":"TEXT","width":300,"required":false}],"minRows":1,"maxRows":20,"allowAdd":true,"allowDelete":true,"showTotal":true,"totalColumn":"amount"}', 1, NOW(), 0),

-- 附件材料
(1737207101007, 1737207001001, '附件材料', 'attachments', 'FILE', 24, b'0', 7, '{"maxCount":5,"maxSize":20,"allowedTypes":[".pdf",".docx",".xlsx",".jpg",".png"],"tips":"支持PDF、Word、Excel、图片格式，单文件最大20MB"}', 1, NOW(), 0);


-- changeset zjx:form-template-data-2
-- comment 初始化表单模板管理菜单

-- 表单模板管理（三级菜单）
INSERT INTO `sys_menu`
(`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `icon`, `is_external`, `is_cache`, `is_hidden`, `sort`, `status`, `create_user`, `create_time`)
VALUES
(1737201291301, '表单模板管理', 1737201291001, 2, '/review/template/form', 'FormTemplate', 'review/template/form/index', 'file-text', b'0', b'1', b'0', 3, 1, 1, NOW());

-- 表单模板管理 - 按钮权限
INSERT INTO `sys_menu`
(`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
(1737201291311, '查询', 1737201291301, 3, 'review:template:form:query', 1, 1, 1, NOW()),
(1737201291312, '创建', 1737201291301, 3, 'review:template:form:create', 2, 1, 1, NOW()),
(1737201291313, '修改', 1737201291301, 3, 'review:template:form:update', 3, 1, 1, NOW()),
(1737201291314, '删除', 1737201291301, 3, 'review:template:form:delete', 4, 1, 1, NOW()),
(1737201291315, '启用/禁用', 1737201291301, 3, 'review:template:form:status', 5, 1, 1, NOW());
