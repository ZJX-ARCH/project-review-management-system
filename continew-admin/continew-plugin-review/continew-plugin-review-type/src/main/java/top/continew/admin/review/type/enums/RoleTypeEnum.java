package top.continew.admin.review.type.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.continew.starter.core.enums.BaseEnum;

/**
 * 人员角色类型枚举
 *
 * @author zjx
 * @since 2026-03-02
 */
@Getter
@RequiredArgsConstructor
public enum RoleTypeEnum implements BaseEnum<String> {

    AUDITOR("AUDITOR", "审核人"),
    REVIEWER("REVIEWER", "评审人"),
    DECISION_MAKER("DECISION_MAKER", "决策人"),
    MANAGER("MANAGER", "管理人"),
    ACCEPTANCE_INSPECTOR("ACCEPTANCE_INSPECTOR", "验收人");

    private final String value;
    private final String description;
}
