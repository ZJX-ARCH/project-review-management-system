package top.continew.admin.review.form.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

/**
 * 表单模板文件请求参数
 *
 * @author zjx
 * @since 2026-01-31
 */
@Data
@Schema(description = "表单模板文件请求参数")
public class FormTemplateFileReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模板ID
     */
    @Schema(description = "模板ID", example = "1001")
    @NotNull(message = "模板ID不能为空")
    private Long templateId;

    /**
     * 字段ID(FILE_TEMPLATE类型字段关联)
     */
    @Schema(description = "字段ID", example = "2001")
    private Long fieldId;

    /**
     * 文件ID(关联sys_file表)
     */
    @Schema(description = "文件ID", example = "5001")
    @NotNull(message = "文件ID不能为空")
    private Long fileId;

    /**
     * 文件类型(TEMPLATE=模板文件,EXAMPLE=示例文件)
     */
    @Schema(description = "文件类型(TEMPLATE/EXAMPLE)", example = "TEMPLATE")
    @NotBlank(message = "文件类型不能为空")
    private String fileType;

    /**
     * 文件说明
     */
    @Schema(description = "文件说明", example = "科研项目申请书标准模板")
    @Length(max = 200, message = "文件说明长度不能超过 {max} 个字符")
    private String description;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "1")
    private Integer sort;
}
