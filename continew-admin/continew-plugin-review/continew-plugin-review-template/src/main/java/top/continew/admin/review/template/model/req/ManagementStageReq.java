package top.continew.admin.review.template.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import top.continew.admin.review.template.enums.StageType;

import java.io.Serial;
import java.io.Serializable;

/**
 * 管理流程阶段请求参数
 *
 * @author zjx
 * @since 2026-01-29
 */
@Data
@Schema(description = "管理流程阶段请求参数")
public class ManagementStageReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 阶段名称
     */
    @Schema(description = "阶段名称", example = "开题阶段")
    @NotBlank(message = "阶段名称不能为空")
    @Length(max = 100, message = "阶段名称长度不能超过 {max} 个字符")
    private String stageName;

    /**
     * 阶段类型（KICKOFF=立项，EXECUTION=执行，ACCEPTANCE=验收）
     */
    @Schema(description = "阶段类型", example = "EXECUTION")
    @NotNull(message = "阶段类型不能为空")
    private StageType stageType;

    /**
     * 阶段顺序（从1开始）
     */
    @Schema(description = "阶段顺序", example = "2")
    @NotNull(message = "阶段顺序不能为空")
    @Min(value = 1, message = "阶段顺序最小值为 {value}")
    private Integer stageOrder;

    /**
     * 是否必须（true=必须，false=可选）
     */
    @Schema(description = "是否必须", example = "true")
    @NotNull(message = "是否必须不能为空")
    private Boolean isRequired;
}
