package top.continew.admin.review.project.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
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
    @Select("SELECT COALESCE(MAX(stage_order), 0) FROM review_project_stage WHERE project_id = #{projectId}")
    Integer selectMaxStageOrder(@Param("projectId") Long projectId);

    /**
     * 软删除指定阶段（绕过 MyBatis Plus 逻辑删除拦截）
     */
    @Update("UPDATE review_project_stage SET deleted = id WHERE id = #{id} AND deleted = 0")
    int softDeleteById(@Param("id") Long id);
}
