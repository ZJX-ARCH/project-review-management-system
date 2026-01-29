package top.continew.admin.review.template.model.resp;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.continew.admin.common.base.model.resp.BaseDetailResp;
import top.continew.admin.common.enums.DisEnableStatusEnum;
import top.continew.starter.excel.converter.ExcelBaseEnumConverter;

import java.io.Serial;
import java.util.List;

/**
 * 评审流程模板响应参数
 *
 * @author zjx
 * @since 2026-01-29
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "评审流程模板响应参数")
public class ProcessTemplateResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模板名称
     */
    @Schema(description = "模板名称", example = "标准评审流程")
    @ExcelProperty(value = "模板名称", order = 2)
    private String templateName;

    /**
     * 模板编码
     */
    @Schema(description = "模板编码", example = "STANDARD_REVIEW")
    @ExcelProperty(value = "模板编码", order = 3)
    private String templateCode;

    /**
     * 模板描述
     */
    @Schema(description = "模板描述", example = "适用于标准项目的评审流程")
    @ExcelProperty(value = "模板描述", order = 4)
    private String description;

    /**
     * 审核轮次
     */
    @Schema(description = "审核轮次", example = "1")
    @ExcelProperty(value = "审核轮次", order = 5)
    private Integer auditRounds;

    /**
     * 评审轮次
     */
    @Schema(description = "评审轮次", example = "2")
    @ExcelProperty(value = "评审轮次", order = 6)
    private Integer reviewRounds;

    /**
     * 决策轮次
     */
    @Schema(description = "决策轮次", example = "1")
    @ExcelProperty(value = "决策轮次", order = 7)
    private Integer decisionRounds;

    /**
     * 状态
     */
    @Schema(description = "状态", example = "1")
    @ExcelProperty(value = "状态", converter = ExcelBaseEnumConverter.class, order = 8)
    private DisEnableStatusEnum status;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "1")
    @ExcelProperty(value = "排序", order = 9)
    private Integer sort;

    /**
     * 轮次名称配置
     */
    @Schema(description = "轮次名称配置")
    private List<RoundNameResp> roundNames;
}
