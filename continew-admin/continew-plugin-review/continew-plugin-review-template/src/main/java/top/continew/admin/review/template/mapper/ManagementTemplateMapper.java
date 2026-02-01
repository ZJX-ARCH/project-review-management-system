package top.continew.admin.review.template.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.continew.admin.common.base.mapper.DataPermissionMapper;
import top.continew.admin.review.template.model.entity.ManagementTemplateDO;

/**
 * 管理流程模板 Mapper
 *
 * @author zjx
 * @since 2026-01-29
 */
@Mapper
public interface ManagementTemplateMapper extends DataPermissionMapper<ManagementTemplateDO> {
}
