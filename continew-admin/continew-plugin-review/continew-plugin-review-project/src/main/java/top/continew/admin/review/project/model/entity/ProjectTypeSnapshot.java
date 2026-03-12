package top.continew.admin.review.project.model.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 类型配置快照（存储于 review_project.snapshot_config JSON 字段）
 * 申请提交时一次性固化，后续流程全程读此快照，不再查 type 模块
 *
 * @author zjx
 * @since 2026-03-07
 */
@Data
public class ProjectTypeSnapshot implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 类型ID */
    private Long typeId;

    /** 类型名称 */
    private String typeName;

    /** 评审流程模板ID */
    private Long reviewProcessTemplateId;

    /** 评审轮次列表（AUDIT/REVIEW/DECISION 按 roundSequence 排列） */
    private List<RoundInfo> rounds;

    /** 管理流程模板ID */
    private Long managementTemplateId;

    /** 管理阶段列表（KICKOFF/EXECUTION/ACCEPTANCE 按 stageOrder 排列） */
    private List<StageInfo> stages;

    /**
     * 表单映射（key=APPLICATION/AUDIT_1/REVIEW_1/DECISION_1/STAGE_1 等，value=表单模板ID）
     */
    private Map<String, Long> formMappings;

    /**
     * 审批规则（key=AUDIT_1/REVIEW_1/DECISION_1/ACCEPTANCE 等，value=审批规则详情）
     */
    private Map<String, ApprovalRuleInfo> approvalRules;

    // ==================== 内部类 ====================

    @Data
    public static class RoundInfo implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /** 轮次类型（AUDIT/REVIEW/DECISION） */
        private String roundType;

        /** 轮次序号（从1开始） */
        private Integer roundSequence;

        /** 轮次名称 */
        private String roundName;
    }

    @Data
    public static class StageInfo implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /** 阶段类型（KICKOFF/EXECUTION/ACCEPTANCE） */
        private String stageType;

        /** 阶段序号（从1开始） */
        private Integer stageOrder;

        /** 阶段名称 */
        private String stageName;

        /** 计划天数（可空） */
        private Integer plannedDays;
    }

    @Data
    public static class ApprovalRuleInfo implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /** 审批模式（VOTE_ALL_PASS/VOTE_MAJORITY_PASS/VOTE_ONE_PASS/SCORE_PASS） */
        private String approvalMode;

        /** 所需审批人数 */
        private Integer requiredReviewerCount;

        /** 多数通过比例（VOTE_MAJORITY_PASS 时有效） */
        private BigDecimal majorityRatio;

        /** 评分通过阈值（SCORE_PASS 时有效） */
        private BigDecimal passThreshold;

        /** 评分计算方式（WEIGHTED_AVG/SIMPLE_AVG/MAX_SCORE/MIN_SCORE），SCORE_PASS 时有效 */
        private String scoreCalcMethod;

        /** 评审人权重模式（EQUAL/PRESET） */
        private String weightMode;

        /** 多个 SCORE_TABLE 字段间的权重（key=fieldCode，value=0~1），仅多表时有效 */
        private Map<String, BigDecimal> scoreTableWeights;
    }
}
