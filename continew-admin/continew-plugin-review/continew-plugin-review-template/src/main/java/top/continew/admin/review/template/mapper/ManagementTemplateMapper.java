package top.continew.admin.review.template.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.continew.admin.review.template.model.entity.ManagementTemplateDO;
import top.continew.starter.data.mapper.BaseMapper;

/**
 * 管理流程模板 Mapper
 *
 * @author zjx
 * @since 2026-01-29
 */
@Mapper
public interface ManagementTemplateMapper extends BaseMapper<ManagementTemplateDO> {
}
