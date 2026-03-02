package top.continew.admin.review.type.model.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.continew.admin.review.type.enums.NodeTypeEnum;
import top.continew.admin.review.type.enums.ProcessTypeEnum;

import java.io.Serial;
import java.io.Serializable;

/**
 * 类型表单映射响应参数（子项）
 *
 * @author zjx
 * @since 2026-03-02
 */
@Data
@Schema(description = "类型表单映射响应参数")
public class TypeFormMappingResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Schema(description = "ID")
    private Long id;

    /**
     * 映射类型（REVIEW=评审流程；MANAGE=管理流程）
     */
    @Schema(description = "映射类型", example = "REVIEW")
    private ProcessTypeEnum mappingType;

    /**
     * 节点类型
     */
    @Schema(description = "节点类型", example = "AUDIT")
    private NodeTypeEnum nodeType;

    /**
     * 节点序号（APPLICATION节点为null，其他从1开始）
     */
    @Schema(description = "节点序号", example = "1")
    private Integer nodeSequence;

    /**
     * 表单模板ID
     */
    @Schema(description = "表单模板ID", example = "1737207001001")
    private Long formTemplateId;

    /**
     * 表单模板名称
     */
    @Schema(description = "表单模板名称", example = "科研项目申请表")
    private String formTemplateName;
}
