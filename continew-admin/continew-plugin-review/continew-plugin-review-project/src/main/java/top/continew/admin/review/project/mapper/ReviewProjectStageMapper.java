package top.continew.admin.review.project.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.continew.admin.review.project.model.entity.ReviewProjectStageDO;
import top.continew.starter.data.mapper.BaseMapper;

/**
 * 管理阶段实例 Mapper
 *
 * @author zjx
 * @since 2026-03-07
 */
@Mapper
public interface ReviewProjectStageMapper extends BaseMapper<ReviewProjectStageDO> {
}
