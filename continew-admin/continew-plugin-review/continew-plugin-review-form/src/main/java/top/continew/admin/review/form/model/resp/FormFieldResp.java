package top.continew.admin.review.form.model.resp;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.continew.admin.review.form.enums.FieldTypeEnum;

import java.io.Serial;
import java.io.Serializable;

/**
 * 表单字段配置响应参数
 *
 * @author zjx
 * @since 2026-01-31
 */
@Data
@Schema(description = "表单字段配置响应参数")
public class FormFieldResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字段ID
     */
    @Schema(description = "字段ID", example = "1001")
    private Long id;

    /**
     * 字段名称
     */
    @Schema(description = "字段名称", example = "项目名称")
    private String fieldName;

    /**
     * 字段编码
     */
    @Schema(description = "字段编码", example = "projectName")
    private String fieldCode;

    /**
     * 字段类型
     */
    @Schema(description = "字段类型", example = "TEXT")
    private FieldTypeEnum fieldType;

    /**
     * 栅格宽度(1-24)
     */
    @Schema(description = "栅格宽度(1-24)", example = "12")
    private Integer span;

    /**
     * 是否必填
     */
    @Schema(description = "是否必填", example = "true")
    private Boolean isRequired;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "1")
    private Integer sort;

    /**
     * 字段配置(JSON格式)
     */
    @Schema(description = "字段配置(JSON格式)", example = "{\"placeholder\":\"请输入项目名称\",\"maxLength\":100}")
    private JsonNode fieldConfig;
}
