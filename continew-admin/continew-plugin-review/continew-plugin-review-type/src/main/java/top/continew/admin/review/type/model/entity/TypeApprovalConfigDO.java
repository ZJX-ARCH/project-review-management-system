package top.continew.admin.review.type.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.continew.admin.common.base.model.entity.BaseDO;
import top.continew.admin.review.type.enums.ApprovalModeEnum;
import top.continew.admin.review.type.enums.ScoreCalcMethodEnum;
import top.continew.admin.review.type.enums.WeightModeEnum;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 类型审批规则配置实体
 *
 * @author zjx
 * @since 2026-03-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "review_type_approval_config", autoResultMap = true)
public class TypeApprovalConfigDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目类型ID
     */
    private Long typeId;

    /**
     * 节点标识（如 AUDIT_1、REVIEW_1、DECISION_1、ACCEPTANCE）
     */
    private String nodeScope;

    /**
     * 审批模式（VOTE_ALL_PASS/VOTE_MAJORITY_PASS/VOTE_ONE_PASS/SCORE_PASS）
     */
    private ApprovalModeEnum approvalMode;

    /**
     * 多数通过比例（0~1），仅 VOTE_MAJORITY_PASS 时有效，默认0.67
     */
    private BigDecimal majorityRatio;

    /**
     * 评分通过阈值，SCORE_PASS 时必填
     */
    private BigDecimal passThreshold;

    /**
     * 良好阈值（可选）
     */
    private BigDecimal goodThreshold;

    /**
     * 优秀阈值（可选）
     */
    private BigDecimal excellentThreshold;

    /**
     * 评分计算方式（WEIGHTED_AVG/SIMPLE_AVG/MAX_SCORE/MIN_SCORE），SCORE_PASS 时必填
     */
    private ScoreCalcMethodEnum scoreCalcMethod;

    /**
     * 评审人权重模式（EQUAL/PRESET），SCORE_PASS 时必填
     */
    private WeightModeEnum weightMode;

    /**
     * 多个SCORE_TABLE字段间的权重，格式：{"fieldCode":0.4}，仅一个SCORE_TABLE时为NULL
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, BigDecimal> scoreTableWeights;

    /**
     * 验收不通过回退目标，仅ACCEPTANCE节点：FIRST_EXECUTION或执行阶段序号（如"3"）
     */
    private String rejectBackTo;
}
