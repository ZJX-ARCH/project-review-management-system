package top.continew.admin.review.type.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import top.continew.admin.review.type.enums.NodeTypeEnum;
import top.continew.admin.review.type.enums.ProcessTypeEnum;

import java.io.Serial;
import java.io.Serializable;

/**
 * 类型表单映射请求参数（单条）
 *
 * @author zjx
 * @since 2026-03-02
 */
@Data
@Schema(description = "类型表单映射请求参数")
public class TypeFormMappingReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 映射类型（REVIEW=评审流程；MANAGE=管理流程）
     */
    @Schema(description = "映射类型（REVIEW/MANAGE）", example = "REVIEW")
    @NotNull(message = "映射类型不能为空")
    private ProcessTypeEnum mappingType;

    /**
     * 节点类型（APPLICATION/AUDIT/REVIEW/DECISION/STAGE）
     */
    @Schema(description = "节点类型", example = "AUDIT")
    @NotNull(message = "节点类型不能为空")
    private NodeTypeEnum nodeType;

    /**
     * 节点序号（APPLICATION节点为NULL，其他从1开始）
     */
    @Schema(description = "节点序号（APPLICATION为null，其他从1开始）", example = "1")
    private Integer nodeSequence;

    /**
     * 表单模板ID
     */
    @Schema(description = "表单模板ID", example = "1737207001001")
    @NotNull(message = "表单模板ID不能为空")
    private Long formTemplateId;
}
