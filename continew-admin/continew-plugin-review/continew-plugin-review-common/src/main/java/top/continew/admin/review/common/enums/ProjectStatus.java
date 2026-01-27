package top.continew.admin.review.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.continew.starter.core.enums.BaseEnum;

/**
 * 项目状态枚举（28个状态）
 *
 * @author zjx
 * @since 2026-01-27
 */
@Getter
@RequiredArgsConstructor
public enum ProjectStatus implements BaseEnum<Integer> {

    // ========== 评审阶段状态（18个） ==========

    DRAFT(1, "草稿", "#E0E0E0"),
    SUBMITTED(2, "已提交", "#FFA500"),

    // 审核流程
    AUDITING(10, "审核中", "#2196F3"),
    AUDIT_PASSED(11, "审核通过", "#4CAF50"),
    NEEDS_REVISION_AUDIT(12, "需修改_审核", "#E0E0E0"),
    AUDIT_REJECTED(13, "审核驳回", "#F44336"),
    WAITING_AUDIT(14, "等待中_审核", "#FFA500"),

    // 评审流程
    REVIEWING(20, "评审中", "#2196F3"),
    REVIEW_PASSED(21, "评审通过", "#4CAF50"),
    NEEDS_REVISION_REVIEW(22, "需修改_评审", "#E0E0E0"),
    REVIEW_REJECTED(23, "评审驳回", "#F44336"),
    WAITING_REVIEW(24, "等待中_评审", "#FFA500"),

    // 决策流程
    DECIDING(30, "决策中", "#2196F3"),
    DECISION_PASSED(31, "决策通过", "#4CAF50"),
    NEEDS_REVISION_DECISION(32, "需修改_决策", "#E0E0E0"),
    DECISION_REJECTED(33, "决策驳回", "#F44336"),
    WAITING_DECISION(34, "等待中_决策", "#FFA500"),

    TERMINATED(40, "已终止", "#FFCCCC"),

    // ========== 项目执行阶段状态（6个） ==========

    EXECUTING(50, "项目执行中", "#2196F3"),
    OVERTIME(51, "已超时", "#FF9800"),
    SUSPENDED(52, "项目暂停", "#FFEB3B"),
    ACCEPTING(53, "项目验收中", "#2196F3"),
    ACCEPTANCE_PASSED(54, "验收通过", "#4CAF50"),
    ACCEPTANCE_FAILED(55, "验收不通过", "#F44336"),

    // ========== 归档阶段状态（4个） ==========

    ARCHIVED_COMPLETED(90, "已归档_已完成", "#1B5E20"),
    ARCHIVED_UNQUALIFIED(91, "已归档_不合格", "#D32F2F"),
    ARCHIVED_CANCELLED(92, "已归档_已取消", "#F57C00"),
    VOIDED(99, "已作废", "#424242");

    private final Integer value;
    private final String description;
    private final String color;

    /**
     * 判断是否为归档状态
     */
    public boolean isArchived() {
        return this == ARCHIVED_COMPLETED
            || this == ARCHIVED_UNQUALIFIED
            || this == ARCHIVED_CANCELLED
            || this == VOIDED;
    }

    /**
     * 判断是否为评审阶段
     */
    public boolean isReviewPhase() {
        return this.value >= 1 && this.value < 50;
    }

    /**
     * 判断是否为执行阶段
     */
    public boolean isExecutionPhase() {
        return this.value >= 50 && this.value < 90;
    }
}
