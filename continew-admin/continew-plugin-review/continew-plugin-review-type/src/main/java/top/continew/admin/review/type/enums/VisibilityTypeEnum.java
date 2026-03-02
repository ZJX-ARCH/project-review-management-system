package top.continew.admin.review.type.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.continew.starter.core.enums.BaseEnum;

/**
 * 可见范围类型枚举
 *
 * @author zjx
 * @since 2026-03-02
 */
@Getter
@RequiredArgsConstructor
public enum VisibilityTypeEnum implements BaseEnum<String> {

    ALL("ALL", "全部可见"),
    DEPT("DEPT", "指定部门"),
    USER("USER", "指定用户");

    private final String value;
    private final String description;
}
