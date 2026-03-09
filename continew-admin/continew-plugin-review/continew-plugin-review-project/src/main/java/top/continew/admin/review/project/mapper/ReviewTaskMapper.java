package top.continew.admin.review.project.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.continew.admin.review.project.model.entity.ReviewTaskDO;
import top.continew.starter.data.mapper.BaseMapper;

/**
 * 任务实例 Mapper
 *
 * @author zjx
 * @since 2026-03-07
 */
@Mapper
public interface ReviewTaskMapper extends BaseMapper<ReviewTaskDO> {

    /**
     * 统计指定用户在同类型任务中的待处理/暂存数量（负载均衡用）
     *
     * @param assigneeId 用户ID
     * @param taskType   任务类型
     * @return 任务数量
     */
    @Select("SELECT COUNT(*) FROM review_task " +
            "WHERE assignee_id = #{assigneeId} " +
            "AND task_type = #{taskType} " +
            "AND status IN ('PENDING', 'SAVED') " +
            "AND deleted = 0")
    int countActiveTasks(@Param("assigneeId") Long assigneeId,
                         @Param("taskType") String taskType);
}
