package top.continew.admin.review.template.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.continew.admin.review.template.model.entity.ProcessTemplateDO;
import top.continew.starter.data.mapper.BaseMapper;

/**
 * 评审流程模板 Mapper
 *
 * @author zjx
 * @since 2026-01-29
 */
@Mapper
public interface ProcessTemplateMapper extends BaseMapper<ProcessTemplateDO> {
}
