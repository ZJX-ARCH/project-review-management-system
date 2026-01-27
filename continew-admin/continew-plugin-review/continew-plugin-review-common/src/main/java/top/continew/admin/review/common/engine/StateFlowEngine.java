package top.continew.admin.review.common.engine;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.continew.admin.review.common.enums.ProjectStatus;
import top.continew.starter.core.exception.BusinessException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 状态流转引擎
 *
 * 核心功能：
 * 1. 验证状态流转规则
 * 2. 执行状态流转
 * 3. 记录状态历史
 * 4. 执行后置动作
 *
 * @author zjx
 * @since 2026-01-27
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StateFlowEngine {

    /**
     * 状态流转规则映射表
     * key: 当前状态
     * value: 可以流转到的目标状态集合
     */
    private static final Map<ProjectStatus, Set<ProjectStatus>> TRANSITION_RULES = new HashMap<>();

    static {
        // 草稿 → 已提交/已作废
        TRANSITION_RULES.put(ProjectStatus.DRAFT, Set.of(
            ProjectStatus.SUBMITTED,
            ProjectStatus.VOIDED
        ));

        // 已提交 → 审核中/评审中/决策中（根据流程配置）
        TRANSITION_RULES.put(ProjectStatus.SUBMITTED, Set.of(
            ProjectStatus.AUDITING,
            ProjectStatus.REVIEWING,
            ProjectStatus.DECIDING
        ));

        // 审核中 → 审核通过/需修改_审核/审核驳回/等待中_审核
        TRANSITION_RULES.put(ProjectStatus.AUDITING, Set.of(
            ProjectStatus.AUDIT_PASSED,
            ProjectStatus.NEEDS_REVISION_AUDIT,
            ProjectStatus.AUDIT_REJECTED,
            ProjectStatus.WAITING_AUDIT
        ));

        // 审核通过 → 评审中/决策中
        TRANSITION_RULES.put(ProjectStatus.AUDIT_PASSED, Set.of(
            ProjectStatus.REVIEWING,
            ProjectStatus.DECIDING
        ));

        // 需修改_审核 → 审核中
        TRANSITION_RULES.put(ProjectStatus.NEEDS_REVISION_AUDIT, Set.of(
            ProjectStatus.AUDITING
        ));

        // 审核驳回 → 已终止
        TRANSITION_RULES.put(ProjectStatus.AUDIT_REJECTED, Set.of(
            ProjectStatus.TERMINATED
        ));

        // 等待中_审核 → 审核中
        TRANSITION_RULES.put(ProjectStatus.WAITING_AUDIT, Set.of(
            ProjectStatus.AUDITING
        ));

        // 评审中 → 评审通过/需修改_评审/评审驳回/等待中_评审
        TRANSITION_RULES.put(ProjectStatus.REVIEWING, Set.of(
            ProjectStatus.REVIEW_PASSED,
            ProjectStatus.NEEDS_REVISION_REVIEW,
            ProjectStatus.REVIEW_REJECTED,
            ProjectStatus.WAITING_REVIEW
        ));

        // 评审通过 → 决策中
        TRANSITION_RULES.put(ProjectStatus.REVIEW_PASSED, Set.of(
            ProjectStatus.DECIDING
        ));

        // 需修改_评审 → 评审中
        TRANSITION_RULES.put(ProjectStatus.NEEDS_REVISION_REVIEW, Set.of(
            ProjectStatus.REVIEWING
        ));

        // 评审驳回 → 已终止
        TRANSITION_RULES.put(ProjectStatus.REVIEW_REJECTED, Set.of(
            ProjectStatus.TERMINATED
        ));

        // 等待中_评审 → 评审中
        TRANSITION_RULES.put(ProjectStatus.WAITING_REVIEW, Set.of(
            ProjectStatus.REVIEWING
        ));

        // 决策中 → 决策通过/需修改_决策/决策驳回/等待中_决策
        TRANSITION_RULES.put(ProjectStatus.DECIDING, Set.of(
            ProjectStatus.DECISION_PASSED,
            ProjectStatus.NEEDS_REVISION_DECISION,
            ProjectStatus.DECISION_REJECTED,
            ProjectStatus.WAITING_DECISION
        ));

        // 决策通过 → 项目执行中
        TRANSITION_RULES.put(ProjectStatus.DECISION_PASSED, Set.of(
            ProjectStatus.EXECUTING
        ));

        // 需修改_决策 → 决策中
        TRANSITION_RULES.put(ProjectStatus.NEEDS_REVISION_DECISION, Set.of(
            ProjectStatus.DECIDING
        ));

        // 决策驳回 → 已终止
        TRANSITION_RULES.put(ProjectStatus.DECISION_REJECTED, Set.of(
            ProjectStatus.TERMINATED
        ));

        // 等待中_决策 → 决策中
        TRANSITION_RULES.put(ProjectStatus.WAITING_DECISION, Set.of(
            ProjectStatus.DECIDING
        ));

        // 已终止 → 已归档_已取消
        TRANSITION_RULES.put(ProjectStatus.TERMINATED, Set.of(
            ProjectStatus.ARCHIVED_CANCELLED
        ));

        // 项目执行中 → 已超时/项目暂停/项目验收中/已归档_已取消
        TRANSITION_RULES.put(ProjectStatus.EXECUTING, Set.of(
            ProjectStatus.OVERTIME,
            ProjectStatus.SUSPENDED,
            ProjectStatus.ACCEPTING,
            ProjectStatus.ARCHIVED_CANCELLED
        ));

        // 已超时 → 项目执行中/项目验收中/已归档_不合格
        TRANSITION_RULES.put(ProjectStatus.OVERTIME, Set.of(
            ProjectStatus.EXECUTING,
            ProjectStatus.ACCEPTING,
            ProjectStatus.ARCHIVED_UNQUALIFIED
        ));

        // 项目暂停 → 项目执行中/已归档_已取消
        TRANSITION_RULES.put(ProjectStatus.SUSPENDED, Set.of(
            ProjectStatus.EXECUTING,
            ProjectStatus.ARCHIVED_CANCELLED
        ));

        // 项目验收中 → 验收通过/验收不通过
        TRANSITION_RULES.put(ProjectStatus.ACCEPTING, Set.of(
            ProjectStatus.ACCEPTANCE_PASSED,
            ProjectStatus.ACCEPTANCE_FAILED
        ));

        // 验收通过 → 已归档_已完成/已归档_不合格/已归档_已取消
        TRANSITION_RULES.put(ProjectStatus.ACCEPTANCE_PASSED, Set.of(
            ProjectStatus.ARCHIVED_COMPLETED,
            ProjectStatus.ARCHIVED_UNQUALIFIED,
            ProjectStatus.ARCHIVED_CANCELLED
        ));

        // 验收不通过 → 项目执行中/已归档_不合格
        TRANSITION_RULES.put(ProjectStatus.ACCEPTANCE_FAILED, Set.of(
            ProjectStatus.EXECUTING,
            ProjectStatus.ARCHIVED_UNQUALIFIED
        ));
    }

    /**
     * 验证状态流转是否合法
     */
    public boolean isValidTransition(ProjectStatus from, ProjectStatus to) {
        if (from == null || to == null) {
            return false;
        }

        Set<ProjectStatus> allowedTargets = TRANSITION_RULES.get(from);
        if (allowedTargets == null) {
            return false;
        }

        return allowedTargets.contains(to);
    }

    /**
     * 执行状态流转
     *
     * @param projectId 项目ID
     * @param currentStatus 当前状态
     * @param targetStatus 目标状态
     * @param reason 流转原因
     */
    public void transition(Long projectId, ProjectStatus currentStatus,
                          ProjectStatus targetStatus, String reason) {
        // 1. 验证流转规则
        if (!isValidTransition(currentStatus, targetStatus)) {
            throw new BusinessException(StrUtil.format(
                "非法的状态流转: {} -> {}",
                currentStatus.getDescription(),
                targetStatus.getDescription()
            ));
        }

        log.info("项目状态流转: projectId={}, from={}, to={}, reason={}",
            projectId, currentStatus, targetStatus, reason);

        // 2. 记录状态历史（由调用方在数据库层面实现）
        // 此处只负责规则验证，不涉及数据库操作

        // 3. 执行后置动作（由调用方根据业务需求实现）
        // 例如：发送通知、触发工作流等
    }

    /**
     * 获取当前状态可以流转到的目标状态列表
     */
    public Set<ProjectStatus> getAvailableTargets(ProjectStatus currentStatus) {
        return TRANSITION_RULES.getOrDefault(currentStatus, Set.of());
    }
}
