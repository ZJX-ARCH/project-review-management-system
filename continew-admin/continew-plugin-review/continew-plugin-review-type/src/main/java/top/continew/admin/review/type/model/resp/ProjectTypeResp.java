package top.continew.admin.review.type.model.resp;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.continew.admin.common.base.model.resp.BaseDetailResp;
import top.continew.admin.review.type.enums.TypeStatusEnum;
import top.continew.starter.excel.converter.ExcelBaseEnumConverter;

import java.io.Serial;

/**
 * 项目类型列表响应参数
 *
 * @author zjx
 * @since 2026-03-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ExcelIgnoreUnannotated
@Schema(description = "项目类型列表响应参数")
public class ProjectTypeResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 类型名称
     */
    @Schema(description = "类型名称", example = "科研项目")
    @ExcelProperty(value = "类型名称", order = 2)
    private String typeName;

    /**
     * 类型编码
     */
    @Schema(description = "类型编码", example = "RESEARCH_PROJECT")
    @ExcelProperty(value = "类型编码", order = 3)
    private String typeCode;

    /**
     * 描述
     */
    @Schema(description = "描述", example = "用于管理科研项目全生命周期")
    @ExcelProperty(value = "描述", order = 4)
    private String description;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "1")
    @ExcelProperty(value = "排序", order = 5)
    private Integer sortOrder;

    /**
     * 状态（0=草稿；1=已启用；2=已禁用）
     */
    @Schema(description = "状态", example = "1")
    @ExcelProperty(value = "状态", converter = ExcelBaseEnumConverter.class, order = 6)
    private TypeStatusEnum status;

    /**
     * 所属部门ID
     */
    @Schema(description = "所属部门ID", example = "1")
    private Long deptId;
}
