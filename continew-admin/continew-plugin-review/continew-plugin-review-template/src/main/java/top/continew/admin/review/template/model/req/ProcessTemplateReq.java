package top.continew.admin.review.template.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 评审流程模板创建或修改请求参数
 *
 * @author zjx
 * @since 2026-01-29
 */
@Data
@Schema(description = "评审流程模板创建或修改请求参数")
public class ProcessTemplateReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模板名称
     */
    @Schema(description = "模板名称", example = "标准评审流程")
    @NotBlank(message = "模板名称不能为空")
    @Length(max = 100, message = "模板名称长度不能超过 {max} 个字符")
    private String templateName;

    /**
     * 模板编码（不填写则自动生成：PROC_ + 时间戳）
     */
    @Schema(description = "模板编码（可选，不填自动生成）", example = "PROC_STANDARD")
    @Pattern(regexp = "^PROC_[A-Z0-9_]*$", message = "模板编码必须以PROC_开头，后续只能包含大写字母、数字和下划线")
    @Length(max = 20, message = "模板编码长度不能超过 {max} 个字符")
    private String templateCode;

    /**
     * 模板描述
     */
    @Schema(description = "模板描述", example = "适用于标准项目的评审流程")
    @Length(max = 500, message = "模板描述长度不能超过 {max} 个字符")
    private String description;

    /**
     * 审核轮次（0-10，0表示跳过）
     */
    @Schema(description = "审核轮次", example = "1")
    @NotNull(message = "审核轮次不能为空")
    @Min(value = 0, message = "审核轮次最小值为 {value}")
    @Max(value = 10, message = "审核轮次最大值为 {value}")
    private Integer auditRounds;

    /**
     * 评审轮次（0-10，0表示跳过）
     */
    @Schema(description = "评审轮次", example = "2")
    @NotNull(message = "评审轮次不能为空")
    @Min(value = 0, message = "评审轮次最小值为 {value}")
    @Max(value = 10, message = "评审轮次最大值为 {value}")
    private Integer reviewRounds;

    /**
     * 决策轮次（1-10，至少1轮）
     */
    @Schema(description = "决策轮次", example = "1")
    @NotNull(message = "决策轮次不能为空")
    @Min(value = 1, message = "决策轮次最小值为 {value}")
    @Max(value = 10, message = "决策轮次最大值为 {value}")
    private Integer decisionRounds;

    /**
     * 轮次名称配置
     */
    @Schema(description = "轮次名称配置")
    @Valid
    private List<RoundNameReq> roundNames;
}
