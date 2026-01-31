package top.continew.admin.review.form.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.continew.starter.core.enums.BaseEnum;

/**
 * 文件类型枚举
 *
 * @author zjx
 * @since 2026-01-31
 */
@Getter
@RequiredArgsConstructor
public enum FileTypeEnum implements BaseEnum<String> {

    TEMPLATE("TEMPLATE", "模板文件"),
    EXAMPLE("EXAMPLE", "示例文件");

    private final String value;
    private final String description;
}
