package top.continew.admin.review.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.continew.starter.core.enums.BaseEnum;

/**
 * 任务类型枚举
 *
 * @author zjx
 * @since 2026-01-27
 */
@Getter
@RequiredArgsConstructor
public enum TaskType implements BaseEnum<String> {

    AUDIT("AUDIT", "审核任务"),
    REVIEW("REVIEW", "评审任务"),
    DECISION("DECISION", "决策任务"),
    MANAGEMENT("MANAGEMENT", "管理任务"),
    ACCEPTANCE("ACCEPTANCE", "验收任务");

    private final String value;
    private final String description;
}
