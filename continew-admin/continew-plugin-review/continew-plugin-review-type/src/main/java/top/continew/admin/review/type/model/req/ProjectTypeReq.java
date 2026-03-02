package top.continew.admin.review.type.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

/**
 * 项目类型基本信息创建/修改请求参数
 *
 * @author zjx
 * @since 2026-03-02
 */
@Data
@Schema(description = "项目类型创建/修改请求参数")
public class ProjectTypeReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 类型名称
     */
    @Schema(description = "类型名称", example = "科研项目")
    @NotBlank(message = "类型名称不能为空")
    @Length(max = 100, message = "类型名称长度不能超过 {max} 个字符")
    private String typeName;

    /**
     * 类型编码（可选，不填自动生成：TYPE_ + 时间戳；创建后不可修改）
     */
    @Schema(description = "类型编码（可选，不填自动生成，格式：TYPE_开头）", example = "TYPE_RESEARCH")
    @Pattern(regexp = "^TYPE_[A-Z0-9_]*$", message = "类型编码必须以TYPE_开头，后续只能包含大写字母、数字和下划线")
    @Length(max = 50, message = "类型编码长度不能超过 {max} 个字符")
    private String typeCode;

    /**
     * 描述
     */
    @Schema(description = "描述", example = "用于管理科研项目全生命周期")
    @Length(max = 500, message = "描述长度不能超过 {max} 个字符")
    private String description;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "1")
    @Min(value = 0, message = "排序最小值为 {value}")
    private Integer sortOrder;
}
