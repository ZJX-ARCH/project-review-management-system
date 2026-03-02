package top.continew.admin.review.type.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import top.continew.admin.review.type.enums.ProcessTypeEnum;

import java.io.Serial;
import java.io.Serializable;

/**
 * 类型流程配置请求参数（单条）
 *
 * @author zjx
 * @since 2026-03-02
 */
@Data
@Schema(description = "类型流程配置请求参数")
public class TypeProcessConfigReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 流程类型（REVIEW=评审流程；MANAGE=管理流程）
     */
    @Schema(description = "流程类型（REVIEW/MANAGE）", example = "REVIEW")
    @NotNull(message = "流程类型不能为空")
    private ProcessTypeEnum processType;

    /**
     * 流程模板ID
     */
    @Schema(description = "流程模板ID", example = "1737205001001")
    @NotNull(message = "流程模板ID不能为空")
    private Long templateId;
}
