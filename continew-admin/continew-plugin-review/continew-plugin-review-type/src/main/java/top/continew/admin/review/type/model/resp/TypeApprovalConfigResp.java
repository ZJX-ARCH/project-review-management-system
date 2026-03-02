package top.continew.admin.review.type.model.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.continew.admin.review.type.enums.ApprovalModeEnum;
import top.continew.admin.review.type.enums.ScoreCalcMethodEnum;
import top.continew.admin.review.type.enums.WeightModeEnum;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 类型审批规则配置响应参数（子项）
 *
 * @author zjx
 * @since 2026-03-02
 */
@Data
@Schema(description = "类型审批规则配置响应参数")
public class TypeApprovalConfigResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Schema(description = "ID")
    private Long id;

    /**
     * 节点标识（如 AUDIT_1、REVIEW_1、DECISION_1、ACCEPTANCE）
     */
    @Schema(description = "节点标识", example = "AUDIT_1")
    private String nodeScope;

    /**
     * 审批模式
     */
    @Schema(description = "审批模式", example = "VOTE_MAJORITY_PASS")
    private ApprovalModeEnum approvalMode;

    /**
     * 多数通过比例（0~1），仅 VOTE_MAJORITY_PASS 时有效
     */
    @Schema(description = "多数通过比例", example = "0.67")
    private BigDecimal majorityRatio;

    /**
     * 评分通过阈值
     */
    @Schema(description = "评分通过阈值", example = "60.00")
    private BigDecimal passThreshold;

    /**
     * 良好阈值
     */
    @Schema(description = "良好阈值", example = "75.00")
    private BigDecimal goodThreshold;

    /**
     * 优秀阈值
     */
    @Schema(description = "优秀阈值", example = "85.00")
    private BigDecimal excellentThreshold;

    /**
     * 评分计算方式
     */
    @Schema(description = "评分计算方式", example = "WEIGHTED_AVG")
    private ScoreCalcMethodEnum scoreCalcMethod;

    /**
     * 评审人权重模式
     */
    @Schema(description = "评审人权重模式", example = "EQUAL")
    private WeightModeEnum weightMode;

    /**
     * 评分表字段权重（多个SCORE_TABLE时）
     */
    @Schema(description = "评分表字段权重", example = "{\"score1\":0.4,\"score2\":0.6}")
    private Map<String, BigDecimal> scoreTableWeights;

    /**
     * 验收不通过回退目标（仅ACCEPTANCE节点）
     */
    @Schema(description = "验收不通过回退目标", example = "FIRST_EXECUTION")
    private String rejectBackTo;

    /**
     * 评审人预设权重列表（PRESET 模式时填充）
     */
    @Schema(description = "评审人预设权重列表（PRESET模式）")
    private List<TypeReviewerWeightResp> reviewerWeights;
}
