package top.continew.admin.review.template.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.continew.admin.common.enums.DisEnableStatusEnum;

import java.io.Serial;
import java.io.Serializable;

/**
 * 管理流程模板查询条件
 *
 * @author zjx
 * @since 2026-01-29
 */
@Data
@Schema(description = "管理流程模板查询条件")
public class ManagementTemplateQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模板名称（模糊搜索）
     */
    @Schema(description = "模板名称", example = "标准管理流程")
    private String templateName;

    /**
     * 模板编码（模糊搜索）
     */
    @Schema(description = "模板编码", example = "MGMT_")
    private String templateCode;

    /**
     * 状态
     */
    @Schema(description = "状态（1=启用，2=禁用）", example = "1")
    private DisEnableStatusEnum status;
}
