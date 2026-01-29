package top.continew.admin.review.template.model.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.continew.admin.review.template.enums.RoundType;

import java.io.Serial;
import java.io.Serializable;

/**
 * 轮次名称响应参数
 *
 * @author zjx
 * @since 2026-01-29
 */
@Data
@Schema(description = "轮次名称响应参数")
public class RoundNameResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 轮次类型（AUDIT=审核,REVIEW=评审,DECISION=决策）
     */
    @Schema(description = "轮次类型", example = "AUDIT")
    private RoundType roundType;

    /**
     * 轮次序号（第几轮，从1开始）
     */
    @Schema(description = "轮次序号", example = "1")
    private Integer roundSequence;

    /**
     * 轮次名称
     */
    @Schema(description = "轮次名称", example = "初审")
    private String roundName;
}
