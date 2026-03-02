package top.continew.admin.review.type.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.continew.starter.core.enums.BaseEnum;

/**
 * 流程类型枚举
 *
 * @author zjx
 * @since 2026-03-02
 */
@Getter
@RequiredArgsConstructor
public enum ProcessTypeEnum implements BaseEnum<String> {

    REVIEW("REVIEW", "评审流程"),
    MANAGE("MANAGE", "管理流程");

    private final String value;
    private final String description;
}
