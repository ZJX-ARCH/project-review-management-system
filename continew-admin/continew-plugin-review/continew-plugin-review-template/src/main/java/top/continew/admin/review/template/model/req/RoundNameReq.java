package top.continew.admin.review.template.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import top.continew.admin.review.template.enums.RoundType;

import java.io.Serial;
import java.io.Serializable;

/**
 * 轮次名称请求参数
 *
 * @author zjx
 * @since 2026-01-29
 */
@Data
@Schema(description = "轮次名称请求参数")
public class RoundNameReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 轮次类型（AUDIT=审核,REVIEW=评审,DECISION=决策）
     */
    @Schema(description = "轮次类型", example = "AUDIT")
    @NotNull(message = "轮次类型不能为空")
    private RoundType roundType;

    /**
     * 轮次序号（第几轮，从1开始）
     */
    @Schema(description = "轮次序号", example = "1")
    @NotNull(message = "轮次序号不能为空")
    @Min(value = 1, message = "轮次序号最小值为 {value}")
    private Integer roundSequence;

    /**
     * 轮次名称
     */
    @Schema(description = "轮次名称", example = "初审")
    @NotBlank(message = "轮次名称不能为空")
    @Length(max = 100, message = "轮次名称长度不能超过 {max} 个字符")
    private String roundName;
}
