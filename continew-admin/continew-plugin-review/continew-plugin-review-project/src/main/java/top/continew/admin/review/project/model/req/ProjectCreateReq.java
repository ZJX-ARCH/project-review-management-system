package top.continew.admin.review.project.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

/**
 * 创建项目草稿请求参数（申请人选好类型后填写基本信息）
 *
 * @author zjx
 * @since 2026-03-07
 */
@Data
@Schema(description = "创建项目草稿请求参数")
public class ProjectCreateReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目类型ID
     */
    @Schema(description = "项目类型ID", example = "1737209001001")
    @NotNull(message = "项目类型不能为空")
    private Long typeId;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称", example = "基于深度学习的图像识别研究")
    @NotBlank(message = "项目名称不能为空")
    @Length(max = 200, message = "项目名称长度不能超过 {max} 个字符")
    private String projectName;

    /**
     * 项目描述
     */
    @Schema(description = "项目描述", example = "本项目旨在研究...")
    private String description;
}
