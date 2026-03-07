package top.continew.admin.review.project.model.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.continew.admin.review.project.enums.StageStatusEnum;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;

/**
 * 管理阶段信息响应参数（被 ProjectDetailResp 和 TaskDetailResp 共用）
 *
 * @author zjx
 * @since 2026-03-07
 */
@Data
@Schema(description = "管理阶段信息")
public class ProjectStageResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 阶段ID
     */
    @Schema(description = "阶段ID", example = "1")
    private Long id;

    /**
     * 阶段类型（KICKOFF/EXECUTION/ACCEPTANCE）
     */
    @Schema(description = "阶段类型", example = "KICKOFF")
    private String stageType;

    /**
     * 阶段序号
     */
    @Schema(description = "阶段序号", example = "1")
    private Integer stageOrder;

    /**
     * 阶段名称
     */
    @Schema(description = "阶段名称", example = "立项")
    private String stageName;

    /**
     * 计划天数
     */
    @Schema(description = "计划天数", example = "30")
    private Integer plannedDays;

    /**
     * 实际开始日期
     */
    @Schema(description = "实际开始日期", example = "2026-03-07")
    private LocalDate startDate;

    /**
     * 计划完成日期
     */
    @Schema(description = "计划完成日期", example = "2026-04-06")
    private LocalDate deadline;

    /**
     * 阶段状态
     */
    @Schema(description = "阶段状态", example = "IN_PROGRESS")
    private StageStatusEnum status;

    /**
     * 是否超时
     */
    @Schema(description = "是否超时", example = "false")
    private Boolean isOverdue;

    /**
     * 申请人提交的阶段成果表单数据（key=字段编码，value=字段值）
     */
    @Schema(description = "阶段成果表单数据")
    private Map<String, Object> stageFormData;
}
