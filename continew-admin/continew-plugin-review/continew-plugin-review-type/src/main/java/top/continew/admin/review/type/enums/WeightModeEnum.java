package top.continew.admin.review.type.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.continew.starter.core.enums.BaseEnum;

/**
 * 评审人权重模式枚举
 *
 * @author zjx
 * @since 2026-03-02
 */
@Getter
@RequiredArgsConstructor
public enum WeightModeEnum implements BaseEnum<String> {

    EQUAL("EQUAL", "等权重"),
    PRESET("PRESET", "预设权重");

    private final String value;
    private final String description;
}
