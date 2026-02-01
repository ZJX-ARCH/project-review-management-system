package top.continew.admin.review.template.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.continew.admin.common.base.mapper.DataPermissionMapper;
import top.continew.admin.review.template.model.entity.ProcessTemplateDO;

/**
 * 评审流程模板 Mapper
 *
 * @author zjx
 * @since 2026-01-29
 */
@Mapper
public interface ProcessTemplateMapper extends DataPermissionMapper<ProcessTemplateDO> {
}
