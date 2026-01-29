package top.continew.admin.review.template.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.continew.starter.core.enums.BaseEnum;

/**
 * 阶段类型枚举
 *
 * @author zjx
 * @since 2026-01-29
 */
@Getter
@RequiredArgsConstructor
public enum StageType implements BaseEnum<String> {

    KICKOFF("KICKOFF", "立项阶段", "固定第一个，不可删除"),
    EXECUTION("EXECUTION", "执行阶段", "中间阶段，可动态增删"),
    ACCEPTANCE("ACCEPTANCE", "验收阶段", "固定最后一个，不可删除");

    private final String value;
    private final String description;
    private final String detail;
}
