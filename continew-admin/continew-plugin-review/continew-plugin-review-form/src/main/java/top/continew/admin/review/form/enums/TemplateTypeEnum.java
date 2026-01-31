package top.continew.admin.review.form.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.continew.starter.core.enums.BaseEnum;

/**
 * 表单模板类型枚举
 *
 * @author zjx
 * @since 2026-01-31
 */
@Getter
@RequiredArgsConstructor
public enum TemplateTypeEnum implements BaseEnum<Integer> {

    APPLICATION(1, "申请表单"),
    AUDIT(2, "审核表单"),
    REVIEW(3, "评审表单"),
    DECISION(4, "决策表单"),
    KICKOFF(5, "立项阶段管理表单"),
    EXECUTION(6, "执行阶段管理表单"),
    ACCEPTANCE(7, "验收阶段管理表单");

    private final Integer value;
    private final String description;
}
