package top.continew.admin.review.type.model.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 评审人预设权重响应参数（PRESET 模式，嵌套在审批规则中）
 *
 * @author zjx
 * @since 2026-03-02
 */
@Data
@Schema(description = "评审人预设权重响应参数")
public class TypeReviewerWeightResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Schema(description = "ID")
    private Long id;

    /**
     * 评审人用户ID
     */
    @Schema(description = "评审人用户ID", example = "1001")
    private Long userId;

    /**
     * 评审人姓名（冗余）
     */
    @Schema(description = "评审人姓名", example = "张三")
    private String userName;

    /**
     * 权重值（0~1）
     */
    @Schema(description = "权重值（0~1）", example = "0.6000")
    private BigDecimal weight;
}
