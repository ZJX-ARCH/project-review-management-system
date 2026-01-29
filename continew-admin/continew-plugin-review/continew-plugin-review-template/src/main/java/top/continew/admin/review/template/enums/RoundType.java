package top.continew.admin.review.template.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.continew.starter.core.enums.BaseEnum;

/**
 * 轮次类型枚举
 *
 * @author zjx
 * @since 2026-01-29
 */
@Getter
@RequiredArgsConstructor
public enum RoundType implements BaseEnum<String> {

    AUDIT("AUDIT", "审核轮次"),
    REVIEW("REVIEW", "评审轮次"),
    DECISION("DECISION", "决策轮次");

    private final String value;
    private final String description;
}
