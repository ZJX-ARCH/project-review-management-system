-- =====================================================
-- 评审模板管理 - 数据字典配置SQL
-- =====================================================
-- 说明：此文件包含评审模板管理模块所需的数据字典配置
-- 使用方法：在后端数据库中执行此SQL，创建必要的字典数据
-- =====================================================

-- 1. 评审模板总分字典
-- 用途：新增/编辑模板时选择总分值
-- 字典编码：evaluation_template_total_score
-- =====================================================
INSERT INTO sys_dict (dict_code, dict_name, description, status, sort, remark, create_time, update_time)
VALUES ('evaluation_template_total_score', '评审模板总分', '评审模板可选的总分值', 1, 1, '用于新增编辑模板时选择总分', NOW(), NOW());

-- 字典项
INSERT INTO sys_dict_item (dict_code, dict_label, dict_value, sort, status, remark, create_time, update_time)
VALUES
  ('evaluation_template_total_score', '100分', '100.00', 1, 1, '标准总分', NOW(), NOW()),
  ('evaluation_template_total_score', '120分', '120.00', 2, 1, '扩展总分', NOW(), NOW()),
  ('evaluation_template_total_score', '150分', '150.00', 3, 1, '高分总分', NOW(), NOW()),
  ('evaluation_template_total_score', '200分', '200.00', 4, 1, '超高分总分', NOW(), NOW());

-- =====================================================
-- 查询验证
-- =====================================================
-- 查询字典
SELECT * FROM sys_dict WHERE dict_code = 'evaluation_template_total_score';

-- 查询字典项
SELECT * FROM sys_dict_item WHERE dict_code = 'evaluation_template_total_score' ORDER BY sort;

-- =====================================================
-- 说明文档
-- =====================================================
/*
数据字典使用规范：

1. 字典编码命名规范：
   - 格式：模块_子模块_字段名
   - 示例：evaluation_template_total_score（评审模板_总分）
   - 使用下划线分隔，全小写

2. 字典项值的类型：
   - 总分使用decimal类型，精确到两位小数（如：100.00）
   - 前端显示时带单位（如：100分）
   - 存储时只存数值（如：100.00）

3. 扩展字典项：
   - 如需添加新的总分选项，直接在 sys_dict_item 表中添加新记录
   - 保持 sort 字段递增，确保显示顺序正确
   - 新增后前端会自动获取最新字典数据（无需修改代码）

4. 前端集成方法：
   参见 BasicInfoStep.vue 中的详细集成说明

5. 其他可能需要的字典：
   - evaluation_template_category_type: 评分大类类型（如：技术类、管理类、综合类）
   - evaluation_template_item_type: 评分项类型（如：必选项、可选项）
   - evaluation_template_status: 模板状态（如：草稿、启用、停用、归档）

   如需添加这些字典，可参照上面的SQL格式创建
*/
