package top.continew.admin.review.project.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 转办任务请求参数
 *
 * @author zjx
 * @since 2026-03-07
 */
@Data
@Schema(description = "转办任务请求参数")
public class TaskTransferReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 转办目标用户ID（须在该节点人员范围内且账号启用）
     */
    @Schema(description = "转办目标用户ID", example = "1000001")
    @NotNull(message = "转办目标用户不能为空")
    private Long targetUserId;

    /**
     * 转办说明（可选）
     */
    @Schema(description = "转办说明", example = "本人近期出差，委托张三代为处理")
    private String transferRemark;
}
