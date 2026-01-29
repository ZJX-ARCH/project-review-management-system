package top.continew.admin.review.template.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

/**
 * 管理流程模板创建或修改请求参数
 *
 * @author zjx
 * @since 2026-01-29
 */
@Data
@Schema(description = "管理流程模板创建或修改请求参数")
public class ManagementTemplateReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模板名称
     */
    @Schema(description = "模板名称", example = "标准管理流程")
    @NotBlank(message = "模板名称不能为空")
    @Length(max = 100, message = "模板名称长度不能超过 {max} 个字符")
    private String templateName;

    /**
     * 模板编码（不填写则自动生成：MGMT_ + 时间戳）
     */
    @Schema(description = "模板编码（可选，不填自动生成，格式：MGMT_开头）", example = "MGMT_STANDARD")
    @Pattern(regexp = "^MGMT_[A-Z_]*$", message = "模板编码必须以MGMT_开头，后续只能包含大写字母和下划线")
    @Length(max = 20, message = "模板编码长度不能超过 {max} 个字符")
    private String templateCode;

    /**
     * 模板描述
     */
    @Schema(description = "模板描述", example = "适用于标准项目的管理流程")
    @Length(max = 500, message = "模板描述长度不能超过 {max} 个字符")
    private String description;
}
