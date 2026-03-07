package top.continew.admin.review.project.model.resp;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.continew.admin.common.base.model.resp.BaseDetailResp;
import top.continew.admin.review.common.enums.TaskType;
import top.continew.admin.review.project.enums.TaskStatusEnum;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 任务列表响应参数（我的任务列表）
 *
 * @author zjx
 * @since 2026-03-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ExcelIgnoreUnannotated
@Schema(description = "任务列表响应参数")
public class TaskListResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    @Schema(description = "项目ID", example = "1")
    private Long projectId;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称", example = "基于深度学习的图像识别研究")
    @ExcelProperty(value = "项目名称", order = 2)
    private String projectName;

    /**
     * 申请人姓名（冗余展示）
     */
    @Schema(description = "申请人姓名", example = "张三")
    @ExcelProperty(value = "申请人", order = 3)
    private String applicantName;

    /**
     * 任务类型
     */
    @Schema(description = "任务类型", example = "AUDIT")
    @ExcelProperty(value = "任务类型", order = 4)
    private TaskType taskType;

    /**
     * 节点序号（第几轮 or 第几阶段）
     */
    @Schema(description = "节点序号", example = "1")
    private Integer nodeSequence;

    /**
     * 节点名称（如"第1轮审核"、"立项阶段"，由 Service 层拼装）
     */
    @Schema(description = "节点名称", example = "第1轮审核")
    @ExcelProperty(value = "节点", order = 5)
    private String nodeName;

    /**
     * 任务状态
     */
    @Schema(description = "任务状态", example = "PENDING")
    @ExcelProperty(value = "状态", order = 6)
    private TaskStatusEnum status;

    /**
     * 分配时间
     */
    @Schema(description = "分配时间", example = "2026-03-07 10:00:00")
    @ExcelProperty(value = "分配时间", order = 7)
    private LocalDateTime assignTime;

    /**
     * 完成时间
     */
    @Schema(description = "完成时间", example = "2026-03-08 10:00:00")
    @ExcelProperty(value = "完成时间", order = 8)
    private LocalDateTime completeTime;
}
