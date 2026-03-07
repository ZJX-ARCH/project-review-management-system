package top.continew.admin.review.project.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.continew.admin.common.base.model.entity.BaseDO;
import top.continew.admin.review.common.enums.TaskType;
import top.continew.admin.review.project.enums.StageStatusEnum;

import java.io.Serial;
import java.time.LocalDate;

/**
 * 管理阶段实例表实体
 *
 * @author zjx
 * @since 2026-03-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "review_project_stage", autoResultMap = true)
public class ReviewProjectStageDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 阶段类型（TaskType.KICKOFF / EXECUTION / ACCEPTANCE）
     */
    private TaskType stageType;

    /**
     * 阶段序号（来自管理流程模板 stage_order）
     */
    private Integer stageOrder;

    /**
     * 阶段名称（来自快照）
     */
    private String stageName;

    /**
     * 计划天数（来自快照，当前模板模块暂无此字段，预留）
     */
    private Integer plannedDays;

    /**
     * 实际开始日期（阶段激活时设置）
     */
    private LocalDate startDate;

    /**
     * 计划完成日期（start_date + planned_days，planned_days 不为空时计算）
     */
    private LocalDate deadline;

    /**
     * 阶段状态（PENDING/IN_PROGRESS/SUBMITTED/COMPLETED/REJECTED）
     */
    private StageStatusEnum status;

    /**
     * 是否超时（0否1是，定时任务维护）
     */
    private Boolean isOverdue;

    /**
     * 申请人提交的阶段成果表单数据（JSON，重做时直接覆盖）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object stageFormData;
}
