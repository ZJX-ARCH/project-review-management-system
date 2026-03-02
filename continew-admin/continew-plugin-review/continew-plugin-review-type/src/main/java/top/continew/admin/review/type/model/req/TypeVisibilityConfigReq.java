package top.continew.admin.review.type.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import top.continew.admin.review.type.enums.VisibilityTypeEnum;

import java.io.Serial;
import java.io.Serializable;

/**
 * 类型可见范围配置请求参数（单条）
 *
 * @author zjx
 * @since 2026-03-02
 */
@Data
@Schema(description = "类型可见范围配置请求参数")
public class TypeVisibilityConfigReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 可见类型（ALL=全部；DEPT=指定部门；USER=指定用户）
     */
    @Schema(description = "可见类型（ALL/DEPT/USER）", example = "DEPT")
    @NotNull(message = "可见类型不能为空")
    private VisibilityTypeEnum visibilityType;

    /**
     * 目标ID（DEPT时为部门ID，USER时为用户ID，ALL时为null）
     */
    @Schema(description = "目标ID（ALL类型时为null）", example = "10")
    private Long targetId;
}
