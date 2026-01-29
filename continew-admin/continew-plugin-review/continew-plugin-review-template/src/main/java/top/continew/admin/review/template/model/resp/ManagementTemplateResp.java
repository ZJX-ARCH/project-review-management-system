package top.continew.admin.review.template.model.resp;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.continew.admin.common.base.model.resp.BaseDetailResp;
import top.continew.admin.common.enums.DisEnableStatusEnum;
import top.continew.starter.excel.converter.ExcelBaseEnumConverter;

import java.io.Serial;

/**
 * 管理流程模板响应参数
 *
 * @author zjx
 * @since 2026-01-29
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "管理流程模板响应参数")
public class ManagementTemplateResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模板名称
     */
    @Schema(description = "模板名称", example = "标准管理流程")
    @ExcelProperty(value = "模板名称", order = 2)
    private String templateName;

    /**
     * 模板编码
     */
    @Schema(description = "模板编码", example = "STANDARD_MANAGEMENT")
    @ExcelProperty(value = "模板编码", order = 3)
    private String templateCode;

    /**
     * 模板描述
     */
    @Schema(description = "模板描述", example = "适用于标准项目的管理流程")
    @ExcelProperty(value = "模板描述", order = 4)
    private String description;

    /**
     * 状态
     */
    @Schema(description = "状态", example = "1")
    @ExcelProperty(value = "状态", converter = ExcelBaseEnumConverter.class, order = 5)
    private DisEnableStatusEnum status;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "1")
    @ExcelProperty(value = "排序", order = 6)
    private Integer sort;
}
