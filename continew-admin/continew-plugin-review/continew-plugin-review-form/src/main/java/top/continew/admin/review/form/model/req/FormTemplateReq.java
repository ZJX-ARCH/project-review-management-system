package top.continew.admin.review.form.model.req;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import top.continew.admin.review.form.enums.TemplateTypeEnum;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 表单模板创建或修改请求参数
 *
 * @author zjx
 * @since 2026-01-31
 */
@Data
@Schema(description = "表单模板创建或修改请求参数")
public class FormTemplateReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模板名称
     */
    @Schema(description = "模板名称", example = "科研项目申请表")
    @NotBlank(message = "模板名称不能为空")
    @Length(max = 100, message = "模板名称长度不能超过 {max} 个字符")
    private String templateName;

    /**
     * 模板编码(不填写则自动生成:FORM_ + 时间戳)
     */
    @Schema(description = "模板编码(可选,不填自动生成)", example = "FORM_RESEARCH_APP")
    @Pattern(regexp = "^FORM_[A-Z0-9_]*$", message = "模板编码必须以FORM_开头,后续只能包含大写字母、数字和下划线")
    @Length(max = 50, message = "模板编码长度不能超过 {max} 个字符")
    private String templateCode;

    /**
     * 模板类型
     */
    @Schema(description = "模板类型(1=申请,2=审核,3=评审,4=决策,5=阶段管理)", example = "1")
    @NotNull(message = "模板类型不能为空")
    private TemplateTypeEnum templateType;

    /**
     * 模板描述
     */
    @Schema(description = "模板描述", example = "适用于科研项目申请")
    @Length(max = 500, message = "模板描述长度不能超过 {max} 个字符")
    private String description;

    /**
     * 布局配置(JSON格式:gridCols、labelWidth、labelAlign)
     */
    @Schema(description = "布局配置(JSON格式)", example = "{\"gridCols\":24,\"labelWidth\":120,\"labelAlign\":\"right\"}")
    private JsonNode layoutConfig;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "1")
    private Integer sort;

    /**
     * 字段配置列表
     */
    @Schema(description = "字段配置列表")
    @Valid
    private List<FormFieldReq> fields;
}
