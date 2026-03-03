package top.continew.admin.review.type.model.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.continew.admin.review.type.enums.ScopeTypeEnum;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * 类型人员范围配置响应参数（子项）
 *
 * @author zjx
 * @since 2026-03-02
 */
@Data
@Schema(description = "类型人员范围配置响应参数")
public class TypePersonnelConfigResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Schema(description = "ID")
    private Long id;

    /**
     * 节点类型
     */
    @Schema(description = "节点类型", example = "AUDIT")
    private String nodeType;

    /**
     * 节点序号
     */
    @Schema(description = "节点序号", example = "1")
    private Integer nodeSequence;

    /**
     * 范围类型
     */
    @Schema(description = "范围类型", example = "DEPT")
    private ScopeTypeEnum scopeType;

    /**
     * 范围配置（JSON）
     */
    @Schema(description = "范围配置JSON", example = "{\"deptIds\":[1],\"includeSub\":false}")
    private Map<String, Object> scopeConfig;

    /**
     * 备注说明
     */
    @Schema(description = "备注说明", example = "科研处全体成员")
    private String remark;
}
