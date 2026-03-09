package top.continew.admin.review.project.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.continew.admin.common.base.mapper.DataPermissionMapper;
import top.continew.admin.review.project.model.entity.ReviewProjectDO;

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
}
