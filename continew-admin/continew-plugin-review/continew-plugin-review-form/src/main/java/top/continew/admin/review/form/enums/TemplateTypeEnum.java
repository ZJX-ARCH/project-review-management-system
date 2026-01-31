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
    STAGE(5, "阶段管理表单");

    private final Integer value;
    private final String description;
}
