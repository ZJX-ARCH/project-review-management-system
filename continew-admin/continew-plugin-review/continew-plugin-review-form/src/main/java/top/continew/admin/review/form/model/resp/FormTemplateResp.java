package top.continew.admin.review.form.model.resp;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.continew.admin.common.base.model.resp.BaseDetailResp;
import top.continew.admin.common.enums.DisEnableStatusEnum;
import top.continew.admin.review.form.enums.TemplateTypeEnum;
import top.continew.starter.excel.converter.ExcelBaseEnumConverter;

import java.io.Serial;
import java.util.List;

/**
 * 表单模板响应参数
 *
 * @author zjx
 * @since 2026-01-31
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "表单模板响应参数")
public class FormTemplateResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模板名称
     */
    @Schema(description = "模板名称", example = "科研项目申请表")
    @ExcelProperty(value = "模板名称", order = 2)
    private String templateName;

    /**
     * 模板编码
     */
    @Schema(description = "模板编码", example = "FORM_RESEARCH_APP")
    @ExcelProperty(value = "模板编码", order = 3)
    private String templateCode;

    /**
     * 模板类型
     */
    @Schema(description = "模板类型", example = "1")
    @ExcelProperty(value = "模板类型", converter = ExcelBaseEnumConverter.class, order = 4)
    private TemplateTypeEnum templateType;

    /**
     * 模板描述
     */
    @Schema(description = "模板描述", example = "适用于科研项目申请")
    @ExcelProperty(value = "模板描述", order = 5)
    private String description;

    /**
     * 布局配置(JSON格式)
     */
    @Schema(description = "布局配置(JSON格式)", example = "{\"gridCols\":24,\"labelWidth\":120,\"labelAlign\":\"right\"}")
    private JsonNode layoutConfig;

    /**
     * 状态
     */
    @Schema(description = "状态", example = "1")
    @ExcelProperty(value = "状态", converter = ExcelBaseEnumConverter.class, order = 6)
    private DisEnableStatusEnum status;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "1")
    @ExcelProperty(value = "排序", order = 7)
    private Integer sort;

    /**
     * 字段配置列表
     */
    @Schema(description = "字段配置列表")
    private List<FormFieldResp> fields;

    /**
     * 附件文件列表
     */
    @Schema(description = "附件文件列表")
    private List<FormTemplateFileResp> files;

    /**
     * 字段数量
     */
    @Schema(description = "字段数量", example = "7")
    private Integer fieldCount;
}
