package top.continew.admin.review.project.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.continew.starter.core.enums.BaseEnum;

/**
 * 任务决策结果枚举
 *
 * @author zjx
 * @since 2026-03-07
 */
@Getter
@RequiredArgsConstructor
public enum TaskDecisionEnum implements BaseEnum<String> {

    /** 通过（评审/管理阶段均可用） */
    PASS("PASS", "通过"),

    /** 驳回（评审阶段：项目终止；管理阶段：当前阶段重做；验收阶段：回退到指定阶段） */
    REJECT("REJECT", "驳回"),

    /** 不合格（仅管理阶段，导致项目归档-不合格） */
    UNQUALIFIED("UNQUALIFIED", "不合格"),

    /** 撤销（仅管理阶段，申请人/管理员主动撤销项目，导致项目归档-已取消；与 TaskStatusEnum.CANCELLED 区分） */
    WITHDRAW("WITHDRAW", "撤销");

    private final String value;
    private final String description;
}
