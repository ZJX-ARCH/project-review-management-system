package top.continew.admin.review.project.model.resp;

import cn.crane4j.annotation.Assemble;
import cn.crane4j.annotation.Mapping;
import cn.crane4j.annotation.condition.ConditionOnPropertyNotNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.continew.admin.common.constant.ContainerConstants;
import top.continew.admin.review.common.enums.TaskType;
import top.continew.admin.review.form.model.resp.FormTemplateResp;
import top.continew.admin.review.project.enums.TaskDecisionEnum;
import top.continew.admin.review.project.enums.TaskStatusEnum;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 任务详情响应参数（处理人视角：项目信息 + 历史节点 + 阶段成果 + 当前任务表单）
 *
 * @author zjx
 * @since 2026-03-07
 */
@Data
@Schema(description = "任务详情响应参数")
public class TaskDetailResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // ==================== 当前任务信息 ====================

    /**
     * 任务ID
     */
    @Schema(description = "任务ID", example = "1")
    private Long taskId;

    /**
     * 任务类型
     */
    @Schema(description = "任务类型", example = "AUDIT")
    private TaskType taskType;

    /**
     * 节点序号
     */
    @Schema(description = "节点序号", example = "1")
    private Integer nodeSequence;

    /**
     * 节点名称（如"第1轮审核"、"立项阶段验收"）
     */
    @Schema(description = "节点名称", example = "第1轮审核")
    private String nodeName;

    /**
     * 任务状态
     */
    @Schema(description = "任务状态", example = "PENDING")
    private TaskStatusEnum taskStatus;

    /**
     * 分配时间
     */
    @Schema(description = "分配时间", example = "2026-03-07 10:00:00")
    private LocalDateTime assignTime;

    /**
     * 本人已暂存的任务表单数据（上次暂存内容，key=字段编码，value=字段值）
     */
    @Schema(description = "本人已暂存的任务表单数据")
    private Map<String, Object> savedFormData;

    /**
     * 当前节点的任务表单模板（用于前端渲染填写区域）
     */
    @Schema(description = "当前节点任务表单模板")
    private FormTemplateResp taskFormTemplate;

    // ==================== 项目基本信息（只读，供参考） ====================

    /**
     * 项目ID
     */
    @Schema(description = "项目ID", example = "1")
    private Long projectId;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称", example = "基于深度学习的图像识别研究")
    private String projectName;

    /**
     * 项目描述
     */
    @Schema(description = "项目描述")
    private String description;

    /**
     * 申请人用户ID（crane4j 自动填充 applicantName）
     */
    @JsonIgnore
    @ConditionOnPropertyNotNull
    @Assemble(container = ContainerConstants.USER_NICKNAME, props = @Mapping(ref = "applicantName"))
    private Long applicantId;

    /**
     * 申请人姓名
     */
    @Schema(description = "申请人姓名", example = "张三")
    private String applicantName;

    /**
     * 提交时间
     */
    @Schema(description = "提交时间", example = "2026-03-07 10:00:00")
    private LocalDateTime submittedTime;

    /**
     * 申请表单填写数据（只读，key=字段编码，value=字段值）
     */
    @Schema(description = "申请表单填写数据（只读）")
    private Map<String, Object> applicationFormData;

    /**
     * 申请表单模板（用于前端渲染只读的申请内容）
     */
    @Schema(description = "申请表单模板（只读渲染用）")
    private FormTemplateResp applicationFormTemplate;

    // ==================== 历史节点汇总（只读，供参考） ====================

    /**
     * 前序节点汇总列表（评审阶段处理人可查看前几轮的结论）
     */
    @Schema(description = "前序节点汇总列表")
    private List<NodeSummaryResp> previousNodes;

    // ==================== 阶段成果（管理/验收阶段处理人专用） ====================

    /**
     * 当前阶段成果（MANAGEMENT 任务专用：管理员审核申请人提交的阶段成果）
     */
    @Schema(description = "当前阶段成果（MANAGEMENT 任务使用）")
    private ProjectStageResp currentStage;

    /**
     * 全部阶段成果列表（ACCEPTANCE 任务专用：验收人员全面查阅所有阶段的成果）
     */
    @Schema(description = "全部阶段成果列表（ACCEPTANCE 任务使用）")
    private List<ProjectStageResp> allStages;

    // ==================== 内部类：历史节点汇总 ====================

    /**
     * 历史节点汇总信息
     */
    @Data
    @Schema(description = "历史节点汇总信息")
    public static class NodeSummaryResp implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 节点类型（AUDIT/REVIEW/DECISION）
         */
        @Schema(description = "节点类型", example = "AUDIT")
        private String nodeType;

        /**
         * 节点序号
         */
        @Schema(description = "节点序号", example = "1")
        private Integer nodeSequence;

        /**
         * 节点名称
         */
        @Schema(description = "节点名称", example = "第1轮审核")
        private String nodeName;

        /**
         * 汇总结果（PASS/REJECT）
         */
        @Schema(description = "汇总结果", example = "PASS")
        private TaskDecisionEnum result;

        /**
         * 通过票数
         */
        @Schema(description = "通过票数", example = "3")
        private Integer passCount;

        /**
         * 总票数
         */
        @Schema(description = "总票数", example = "5")
        private Integer totalCount;

        /**
         * 平均分（SCORE_PASS 模式时有值）
         */
        @Schema(description = "平均分（评分模式）", example = "82.5")
        private BigDecimal averageScore;
    }
}
