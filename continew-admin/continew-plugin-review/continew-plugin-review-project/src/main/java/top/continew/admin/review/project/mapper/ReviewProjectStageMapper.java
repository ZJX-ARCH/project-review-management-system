package top.continew.admin.review.project.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
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

    /**
     * 查询项目的最大阶段序号
     */
    @Select("SELECT COALESCE(MAX(stage_order), 0) FROM review_project_stage WHERE project_id = #{projectId} AND deleted = 0")
    Integer selectMaxStageOrder(@Param("projectId") Long projectId);
}
