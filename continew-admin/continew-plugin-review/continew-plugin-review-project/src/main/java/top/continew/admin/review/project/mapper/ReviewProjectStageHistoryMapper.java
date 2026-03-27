package top.continew.admin.review.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.continew.admin.review.project.model.entity.ReviewProjectStageHistoryDO;

/**
 * 阶段状态历史 Mapper
 *
 * @author zjx
 * @since 2026-03-26
 */
@Mapper
public interface ReviewProjectStageHistoryMapper extends BaseMapper<ReviewProjectStageHistoryDO> {
}
