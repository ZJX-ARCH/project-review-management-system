package top.continew.admin.review.project.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.continew.admin.common.base.model.entity.BaseDO;
import top.continew.admin.review.common.enums.TaskType;
import top.continew.admin.review.project.enums.TaskDecisionEnum;
import top.continew.admin.review.project.enums.TaskStatusEnum;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 任务实例表实体
 *
 * @author zjx
 * @since 2026-03-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "review_task", autoResultMap = true)
public class ReviewTaskDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 任务类型（AUDIT/REVIEW/DECISION/MANAGEMENT/ACCEPTANCE）
     */
    private TaskType taskType;

    /**
     * 节点序号（评审阶段为轮次序号，管理阶段为 stage_order）
     */
    private Integer nodeSequence;

    /**
     * 被分配人用户ID
     */
    private Long assigneeId;

    /**
     * 任务状态（PENDING/SAVED/COMPLETED/TRANSFERRED/CANCELLED）
     */
    private TaskStatusEnum status;

    /**
     * 决策结果（PASS/REJECT/UNQUALIFIED/WITHDRAW）
     */
    private TaskDecisionEnum decision;

    /**
     * 评分（SCORE_PASS 模式时填写）
     */
    private BigDecimal score;

    /**
     * 任务表单填写数据（JSON）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object formData;

    /**
     * 验收任务 REJECT 时指定回退到的阶段序号（仅 ACCEPTANCE 任务 REJECT 时有效）
     */
    private Integer rejectBackToStageOrder;

    /**
     * 转办次数（上限 MAX_TRANSFER_COUNT=3）
     */
    private Integer transferCount;

    /**
     * 转办说明（留存最后一次转办原因）
     */
    private String transferRemark;

    /**
     * 分配时间
     */
    private LocalDateTime assignTime;

    /**
     * 完成时间（提交或转办时记录）
     */
    private LocalDateTime completeTime;
}
