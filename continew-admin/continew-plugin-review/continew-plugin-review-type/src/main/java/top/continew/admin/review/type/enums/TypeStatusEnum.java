package top.continew.admin.review.type.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.continew.starter.core.enums.BaseEnum;

/**
 * 项目类型状态枚举
 *
 * @author zjx
 * @since 2026-03-02
 */
@Getter
@RequiredArgsConstructor
public enum TypeStatusEnum implements BaseEnum<Integer> {

    DRAFT(0, "草稿"),
    ENABLED(1, "已启用"),
    DISABLED(2, "已禁用");

    private final Integer value;
    private final String description;
}
