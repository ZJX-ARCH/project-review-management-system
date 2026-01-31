package top.continew.admin.review.form.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.continew.admin.review.form.model.entity.FormFieldDO;
import top.continew.starter.data.mapper.BaseMapper;

/**
 * 表单字段配置 Mapper
 *
 * @author zjx
 * @since 2026-01-31
 */
@Mapper
public interface FormFieldMapper extends BaseMapper<FormFieldDO> {
}
