package top.continew.admin.review.form.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.continew.admin.common.enums.DisEnableStatusEnum;
import top.continew.admin.review.form.enums.TemplateTypeEnum;

import java.io.Serial;
import java.io.Serializable;

/**
 * 表单模板查询条件
 *
 * @author zjx
 * @since 2026-01-31
 */
@Data
@Schema(description = "表单模板查询条件")
public class FormTemplateQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模板名称(模糊搜索)
     */
    @Schema(description = "模板名称", example = "科研项目申请表")
    private String templateName;

    /**
     * 模板编码(模糊搜索)
     */
    @Schema(description = "模板编码", example = "FORM_")
    private String templateCode;

    /**
     * 模板类型
     */
    @Schema(description = "模板类型(1=申请,2=审核,3=评审,4=决策,5=立项阶段,6=执行阶段,7=验收阶段)", example = "1")
    private TemplateTypeEnum templateType;

    /**
     * 状态
     */
    @Schema(description = "状态(1=启用,2=禁用)", example = "1")
    private DisEnableStatusEnum status;
}
