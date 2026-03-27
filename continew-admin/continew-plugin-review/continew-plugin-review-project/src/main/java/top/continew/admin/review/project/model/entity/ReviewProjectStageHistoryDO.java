package top.continew.admin.review.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import top.continew.admin.review.common.enums.TaskType;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 阶段状态历史表实体（记录驳回历史快照）
 *
 * @author zjx
 * @since 2026-03-26
 */
@Data
@TableName(value = "review_project_stage_history", autoResultMap = true)
public class ReviewProjectStageHistoryDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long projectId;

    private Long stageId;

    private TaskType stageType;

    private Integer stageOrder;

    private String stageName;

    private String oldStatus;

    private String newStatus;

    private LocalDate startDate;

    private LocalDate deadline;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object stageFormData;

    private LocalDateTime changeTime;

    private String remark;
}
