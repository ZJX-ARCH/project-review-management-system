package top.continew.admin.review.project.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.continew.admin.review.project.model.entity.ReviewProjectModifyLogDO;
import top.continew.starter.data.mapper.BaseMapper;

/**
 * 申请表单修改痕迹日志 Mapper
 *
 * @author zjx
 * @since 2026-03-07
 */
@Mapper
public interface ReviewProjectModifyLogMapper extends BaseMapper<ReviewProjectModifyLogDO> {
}
