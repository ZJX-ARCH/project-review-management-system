package top.continew.admin.review.template.model.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.continew.admin.review.template.enums.StageType;

import java.io.Serial;
import java.io.Serializable;

/**
 * 管理流程阶段响应参数
 *
 * @author zjx
 * @since 2026-01-29
 */
@Data
@Schema(description = "管理流程阶段响应参数")
public class ManagementStageResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 阶段名称
     */
    @Schema(description = "阶段名称", example = "开题阶段")
    private String stageName;

    /**
     * 阶段类型（KICKOFF=立项，EXECUTION=执行，ACCEPTANCE=验收）
     */
    @Schema(description = "阶段类型", example = "EXECUTION")
    private StageType stageType;

    /**
     * 阶段顺序（从1开始）
     */
    @Schema(description = "阶段顺序", example = "2")
    private Integer stageOrder;

    /**
     * 是否必须（true=必须，false=可选）
     */
    @Schema(description = "是否必须", example = "true")
    private Boolean isRequired;
}
