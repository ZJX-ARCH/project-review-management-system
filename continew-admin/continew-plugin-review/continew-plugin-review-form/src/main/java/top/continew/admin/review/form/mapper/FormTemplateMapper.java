package top.continew.admin.review.form.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.continew.admin.review.form.model.entity.FormTemplateDO;
import top.continew.starter.data.mapper.BaseMapper;

/**
 * 表单模板 Mapper
 *
 * @author zjx
 * @since 2026-01-31
 */
@Mapper
public interface FormTemplateMapper extends BaseMapper<FormTemplateDO> {
}
