package top.continew.admin.review.type.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 评审人预设权重请求参数（PRESET 模式，嵌套在审批规则中）
 *
 * @author zjx
 * @since 2026-03-02
 */
@Data
@Schema(description = "评审人预设权重请求参数")
public class TypeReviewerWeightReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 评审人用户ID
     */
    @Schema(description = "评审人用户ID", example = "1001")
    @NotNull(message = "评审人用户ID不能为空")
    private Long userId;

    /**
     * 权重值（0~1，同一节点所有人权重之和须=1.0）
     */
    @Schema(description = "权重值（0~1）", example = "0.6000")
    @NotNull(message = "权重值不能为空")
    @DecimalMin(value = "0.0001", message = "权重值最小为 0.0001")
    @DecimalMax(value = "1.0000", message = "权��值最大为 1.0000")
    private BigDecimal weight;
}
