package top.continew.admin.review.project.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.continew.admin.common.base.mapper.DataPermissionMapper;
import top.continew.admin.review.project.model.entity.ReviewProjectDO;

import java.util.List;

/**
 * 项目实例 Mapper
 *
 * @author zjx
 * @since 2026-03-07
 */
@Mapper
public interface ReviewProjectMapper extends DataPermissionMapper<ReviewProjectDO> {

    /**
     * 加行锁查询项目（用于并发汇总时防重复处理）
     *
     * @param id 项目ID
     * @return 项目实体（已锁定）
     */
    @Select("SELECT * FROM review_project WHERE id = #{id} AND deleted = 0 FOR UPDATE")
    ReviewProjectDO selectByIdForUpdate(@Param("id") Long id);

    /**
     * 按项目名称模糊查询ID列表（绕过 @DataPermission，供跨权限场景使用）
     *
     * @param name 项目名称关键词
     * @return 匹配的项目ID列表
     */
    @Select("SELECT id FROM review_project WHERE deleted = 0 AND project_name LIKE CONCAT('%', #{name}, '%')")
    List<Long> selectIdsByNameLike(@Param("name") String name);
}
