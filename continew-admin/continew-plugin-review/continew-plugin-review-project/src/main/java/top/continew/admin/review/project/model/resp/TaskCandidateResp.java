package top.continew.admin.review.project.model.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 转办候选人响应
 *
 * @author zjx
 * @since 2026-03-25
 */
@Data
@Schema(description = "转办候选人")
public class TaskCandidateResp {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户账号")
    private String username;
}
