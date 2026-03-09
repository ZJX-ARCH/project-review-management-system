package top.continew.admin.review.type.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.continew.admin.common.base.mapper.DataPermissionMapper;
import top.continew.admin.review.type.model.entity.ProjectTypeDO;

import java.util.List;

/**
 * 项目类型 Mapper
 *
 * @author zjx
 * @since 2026-03-02
 */
@Mapper
public interface ProjectTypeMapper extends DataPermissionMapper<ProjectTypeDO> {

    /**
     * 查询所有已启用的项目类型（忽略数据权限，供申请人查看可申请类型范围）
     *
     * @return 已启用的类型列表
     */
    @Select("SELECT id, type_name, type_code, description, sort_order, status, dept_id, create_user, create_time, update_user, update_time, deleted FROM review_project_type WHERE deleted = 0 AND status = 1 ORDER BY sort_order ASC")
    List<ProjectTypeDO> selectAllEnabled();
}
