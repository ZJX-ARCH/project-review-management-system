package top.continew.admin.review.project.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * 有痕修改申请表单请求参数（修改前后数据均记录日志）
 *
 * @author zjx
 * @since 2026-03-07
 */
@Data
@Schema(description = "有痕修改申请表单请求参数")
public class ProjectUpdateFormReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 修改后的申请表单数据（key=字段编码，value=字段值）
     */
    @Schema(description = "修改后的申请表单数据")
    @NotNull(message = "表单数据不能为空")
    private Map<String, Object> formData;

    /**
     * 修改原因（可选，建议填写）
     */
    @Schema(description = "修改原因", example = "申请金额填写有误，修正为正确金额")
    private String modifyReason;
}
