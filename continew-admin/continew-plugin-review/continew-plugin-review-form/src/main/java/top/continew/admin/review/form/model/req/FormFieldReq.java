package top.continew.admin.review.form.model.req;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import top.continew.admin.review.form.enums.FieldTypeEnum;

import java.io.Serial;
import java.io.Serializable;

/**
 * 表单字段配置请求参数
 *
 * @author zjx
 * @since 2026-01-31
 */
@Data
@Schema(description = "表单字段配置请求参数")
public class FormFieldReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字段名称
     */
    @Schema(description = "字段名称", example = "项目名称")
    @NotBlank(message = "字段名称不能为空")
    @Length(max = 100, message = "字段名称长度不能超过 {max} 个字符")
    private String fieldName;

    /**
     * 字段编码
     */
    @Schema(description = "字段编码", example = "projectName")
    @NotBlank(message = "字段编码不能为空")
    @Pattern(regexp = "^[a-z][a-zA-Z0-9]*$", message = "字段编码必须以小写字母开头,只能包含字母和数字")
    @Length(max = 50, message = "字段编码长度不能超过 {max} 个字符")
    private String fieldCode;

    /**
     * 字段类型
     */
    @Schema(description = "字段类型", example = "TEXT")
    @NotNull(message = "字段类型不能为空")
    private FieldTypeEnum fieldType;

    /**
     * 栅格宽度(1-24)
     */
    @Schema(description = "栅格宽度(1-24)", example = "12")
    @NotNull(message = "栅格宽度不能为空")
    @Min(value = 1, message = "栅格宽度最小值为 {value}")
    @Max(value = 24, message = "栅格宽度最大值为 {value}")
    private Integer span;

    /**
     * 是否必填
     */
    @Schema(description = "是否必填", example = "true")
    @NotNull(message = "是否必填不能为空")
    private Boolean isRequired;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "1")
    @NotNull(message = "排序不能为空")
    @Min(value = 0, message = "排序最小值为 {value}")
    private Integer sort;

    /**
     * 字段配置(JSON格式,根据字段类型不同而不同)
     */
    @Schema(description = "字段配置(JSON格式)", example = "{\"placeholder\":\"请输入项目名称\",\"maxLength\":100}")
    private JsonNode fieldConfig;
}
