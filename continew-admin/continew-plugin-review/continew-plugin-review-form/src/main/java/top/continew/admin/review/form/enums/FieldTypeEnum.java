package top.continew.admin.review.form.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.continew.starter.core.enums.BaseEnum;

/**
 * 表单字段类型枚举
 *
 * @author zjx
 * @since 2026-01-31
 */
@Getter
@RequiredArgsConstructor
public enum FieldTypeEnum implements BaseEnum<String> {

    TEXT("TEXT", "单行文本"),
    TEXTAREA("TEXTAREA", "多行文本"),
    NUMBER("NUMBER", "数字输入"),
    DATE("DATE", "日期选择"),
    SELECT("SELECT", "下拉选择"),
    RADIO("RADIO", "单选框组"),
    CHECKBOX("CHECKBOX", "多选框组"),
    FILE("FILE", "文件上传"),
    FILE_TEMPLATE("FILE_TEMPLATE", "文件模板"),
    TABLE("TABLE", "动态表格"),
    SCORE("SCORE", "评分组件");

    private final String value;
    private final String description;
}
