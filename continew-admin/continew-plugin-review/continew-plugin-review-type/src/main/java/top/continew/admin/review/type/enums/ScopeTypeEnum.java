package top.continew.admin.review.type.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.continew.starter.core.enums.BaseEnum;

/**
 * 人员来源范围枚举
 *
 * @author zjx
 * @since 2026-03-02
 */
@Getter
@RequiredArgsConstructor
public enum ScopeTypeEnum implements BaseEnum<String> {

    USER("USER", "指定用户"),
    ROLE("ROLE", "指定角色"),
    DEPT("DEPT", "指定部门"),
    COMBINED("COMBINED", "组合来源");

    private final String value;
    private final String description;
}
