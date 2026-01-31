package top.continew.admin.review.form.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.continew.admin.common.base.model.entity.BaseDO;
import top.continew.admin.review.form.enums.FieldTypeEnum;

import java.io.Serial;

/**
 * 表单字段配置实体
 *
 * @author zjx
 * @since 2026-01-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "review_form_field", autoResultMap = true)
public class FormFieldDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模板ID
     */
    private Long templateId;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 字段编码
     */
    private String fieldCode;

    /**
     * 字段类型（TEXT/TEXTAREA/NUMBER/DATE/SELECT/RADIO/CHECKBOX/FILE/FILE_TEMPLATE/TABLE/SCORE）
     */
    private FieldTypeEnum fieldType;

    /**
     * 栅格宽度（1-24）
     */
    private Integer span;

    /**
     * 是否必填
     */
    private Boolean isRequired;

    /**
     * 排序（决定字段显示顺序）
     */
    private Integer sort;

    /**
     * 字段特定配置（根据字段类型不同而不同）
     */
    private String fieldConfig;
}
