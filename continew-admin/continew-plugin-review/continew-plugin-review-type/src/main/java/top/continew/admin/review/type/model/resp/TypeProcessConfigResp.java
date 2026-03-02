package top.continew.admin.review.type.model.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.continew.admin.review.type.enums.ProcessTypeEnum;

import java.io.Serial;
import java.io.Serializable;

/**
 * 类型流程配置响应参数（子项）
 *
 * @author zjx
 * @since 2026-03-02
 */
@Data
@Schema(description = "类型流程配置响应参数")
public class TypeProcessConfigResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Schema(description = "ID")
    private Long id;

    /**
     * 流程类型（REVIEW=评审流程；MANAGE=管理流程）
     */
    @Schema(description = "流程类型", example = "REVIEW")
    private ProcessTypeEnum processType;

    /**
     * 流程模板ID
     */
    @Schema(description = "流程模板ID", example = "1737205001001")
    private Long templateId;

    /**
     * 流程模板名称
     */
    @Schema(description = "流程模板名称", example = "标准三轮审核流程")
    private String templateName;

    /**
     * 是否主流程
     */
    @Schema(description = "是否主流程", example = "true")
    private Boolean isPrimary;
}
