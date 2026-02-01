package top.continew.admin.review.form.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.continew.admin.common.base.mapper.DataPermissionMapper;
import top.continew.admin.review.form.model.entity.FormTemplateDO;

/**
 * 表单模板 Mapper
 *
 * @author zjx
 * @since 2026-01-31
 */
@Mapper
public interface FormTemplateMapper extends DataPermissionMapper<FormTemplateDO> {
}
