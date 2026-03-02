package top.continew.admin.review.type.model.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.continew.admin.review.type.enums.VisibilityTypeEnum;

import java.io.Serial;
import java.io.Serializable;

/**
 * 类型可见范围配置响应参数（子项）
 *
 * @author zjx
 * @since 2026-03-02
 */
@Data
@Schema(description = "类型可见范围配置响应参数")
public class TypeVisibilityConfigResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Schema(description = "ID")
    private Long id;

    /**
     * 可见类型（ALL=全部；DEPT=指定部门；USER=指定用户）
     */
    @Schema(description = "可见类型", example = "DEPT")
    private VisibilityTypeEnum visibilityType;

    /**
     * 目标ID（DEPT为部门ID，USER为用户ID，ALL时为null）
     */
    @Schema(description = "目标ID", example = "10")
    private Long targetId;

    /**
     * 目标名称（冗余）
     */
    @Schema(description = "目标名称", example = "科研处")
    private String targetName;
}
