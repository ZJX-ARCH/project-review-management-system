package top.continew.admin.review.project.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import top.continew.admin.review.project.enums.TaskDecisionEnum;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 提交任务决策请求参数（触发结果汇总与流程推进）
 *
 * @author zjx
 * @since 2026-03-07
 */
@Data
@Schema(description = "提交任务决策请求参数")
public class TaskSubmitReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 决策结果（PASS/REJECT/UNQUALIFIED/WITHDRAW）
     * 评审阶段只能选 PASS/REJECT；管理阶段可选 PASS/REJECT/UNQUALIFIED/WITHDRAW
     */
    @Schema(description = "决策结果（PASS/REJECT/UNQUALIFIED/WITHDRAW）", example = "PASS")
    @NotNull(message = "决策结果不能为空")
    private TaskDecisionEnum decision;

    /**
     * 评分（仅 SCORE_PASS 审批模式时必填）
     */
    @Schema(description = "评分（仅 SCORE_PASS 模式时填写）", example = "85.50")
    @DecimalMin(value = "0", message = "评分不能小于 0")
    @DecimalMax(value = "100", message = "评分不能大于 100")
    private BigDecimal score;

    /**
     * 回退目标阶段序号（仅验收任务 REJECT 时必填，Service 层校验条件必填性）
     */
    @Schema(description = "回退到的阶段序号（仅验收 REJECT 时填写）", example = "2")
    @Min(value = 1, message = "回退阶段序号最小为 1")
    private Integer rejectBackToStageOrder;

    /**
     * 任务表单填写数据（key=字段编码，value=字段值）
     */
    @Schema(description = "任务表单填写数据")
    private Map<String, Object> formData;
}
