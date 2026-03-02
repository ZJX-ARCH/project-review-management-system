package top.continew.admin.review.type.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.continew.admin.review.type.enums.TypeStatusEnum;

import java.io.Serial;
import java.io.Serializable;

/**
 * 项目类型列表查询条件
 *
 * @author zjx
 * @since 2026-03-02
 */
@Data
@Schema(description = "项目类型查询条件")
public class ProjectTypeQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 类型名称（模糊搜索）
     */
    @Schema(description = "类型名称", example = "科研")
    private String typeName;

    /**
     * 类型编码（模糊搜索）
     */
    @Schema(description = "类型编码", example = "RESEARCH_")
    private String typeCode;

    /**
     * 状态（0=草稿；1=已启用；2=已禁用）
     */
    @Schema(description = "状态（0=草稿；1=已启用；2=已禁用）", example = "1")
    private TypeStatusEnum status;
}
