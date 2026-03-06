package top.continew.admin.review.type.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import top.continew.admin.review.type.enums.ApprovalModeEnum;
import top.continew.admin.review.type.enums.ScoreCalcMethodEnum;
import top.continew.admin.review.type.enums.WeightModeEnum;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 类型审批规则配置请求参数（单条）
 *
 * @author zjx
 * @since 2026-03-02
 */
@Data
@Schema(description = "类型审批规则配置请求参数")
public class TypeApprovalConfigReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 节点标识（如 AUDIT_1、REVIEW_1、DECISION_1、ACCEPTANCE）
     */
    @Schema(description = "节点标识", example = "AUDIT_1")
    @NotBlank(message = "节点标识不能为空")
    @Length(max = 30, message = "节点标识长度不能超过 {max} 个字符")
    private String nodeScope;

    /**
     * 所需审批人数（用于校验人员范围是否满足要求）
     */
    @Schema(description = "所需审批人数", example = "3")
    private Integer requiredReviewerCount;

    /**
     * 审批模式（VOTE_ALL_PASS/VOTE_MAJORITY_PASS/VOTE_ONE_PASS/SCORE_PASS）
     */
    @Schema(description = "审批模式", example = "VOTE_MAJORITY_PASS")
    @NotNull(message = "审批模式不能为空")
    private ApprovalModeEnum approvalMode;

    /**
     * 多数通过比例（0~1），仅 VOTE_MAJORITY_PASS 时有效，默认0.67
     */
    @Schema(description = "多数通过比例（0~1），VOTE_MAJORITY_PASS时必填", example = "0.67")
    @DecimalMin(value = "0.01", message = "多数通过比例最小为 0.01")
    @DecimalMax(value = "1.00", message = "多数通过比例最大为 1.00")
    private BigDecimal majorityRatio;

    /**
     * 评分通过阈值，SCORE_PASS 时必填
     */
    @Schema(description = "评分通过阈值，SCORE_PASS时必填", example = "60.00")
    @DecimalMin(value = "0", message = "通过阈值不能为负数")
    private BigDecimal passThreshold;

    /**
     * 良好阈值（可选）
     */
    @Schema(description = "良好阈值（可选）", example = "75.00")
    private BigDecimal goodThreshold;

    /**
     * 优秀阈值（可选）
     */
    @Schema(description = "优秀阈值（可选）", example = "85.00")
    private BigDecimal excellentThreshold;

    /**
     * 评分计算方式，SCORE_PASS 时必填
     */
    @Schema(description = "评分计算方式，SCORE_PASS时必填", example = "WEIGHTED_AVG")
    private ScoreCalcMethodEnum scoreCalcMethod;

    /**
     * 评审人权重模式（EQUAL/PRESET），SCORE_PASS 时必填
     */
    @Schema(description = "评审人权重模式，SCORE_PASS时必填", example = "EQUAL")
    private WeightModeEnum weightMode;

    /**
     * 多个SCORE_TABLE字段间的权重，格式：{"fieldCode":0.4}
     */
    @Schema(description = "评分表字段权重（多个SCORE_TABLE时填写）", example = "{\"score1\":0.4,\"score2\":0.6}")
    private Map<String, BigDecimal> scoreTableWeights;

    /**
     * 评审人预设权重列表（weightMode=PRESET 时必填，所有人权重之和须=1.0）
     */
    @Schema(description = "评审人预设权重列表（PRESET模式时填写）")
    @Valid
    private List<TypeReviewerWeightReq> reviewerWeights;
}
