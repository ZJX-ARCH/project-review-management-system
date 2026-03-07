package top.continew.admin.review.project.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.continew.admin.review.common.enums.TaskType;
import top.continew.admin.review.project.enums.TaskStatusEnum;

import java.io.Serial;
import java.io.Serializable;

/**
 * 任务列表查询条件
 *
 * @author zjx
 * @since 2026-03-07
 */
@Data
@Schema(description = "任务列表查询条件")
public class TaskQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目名称（模糊搜索）
     */
    @Schema(description = "项目名称", example = "科研")
    private String projectName;

    /**
     * 任务类型
     */
    @Schema(description = "任务类型（AUDIT/REVIEW/DECISION/MANAGEMENT/ACCEPTANCE）", example = "AUDIT")
    private TaskType taskType;

    /**
     * 任务状态
     */
    @Schema(description = "任务状态（PENDING/SAVED/COMPLETED/TRANSFERRED/CANCELLED）", example = "PENDING")
    private TaskStatusEnum status;
}
