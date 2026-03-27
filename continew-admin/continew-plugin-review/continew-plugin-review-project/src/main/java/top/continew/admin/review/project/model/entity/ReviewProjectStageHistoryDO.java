package top.continew.admin.review.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 阶段状态历史表实体
 *
 * @author zjx
 * @since 2026-03-26
 */
@Data
@TableName("review_project_stage_history")
public class ReviewProjectStageHistoryDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long stageId;

    private String oldStatus;

    private String newStatus;

    private LocalDateTime changeTime;

    private String remark;
}
