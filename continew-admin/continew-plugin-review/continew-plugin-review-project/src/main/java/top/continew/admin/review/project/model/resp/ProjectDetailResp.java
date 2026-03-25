package top.continew.admin.review.project.model.resp;

import cn.crane4j.annotation.Assemble;
import cn.crane4j.annotation.Mapping;
import cn.crane4j.annotation.condition.ConditionOnPropertyNotNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.continew.admin.common.base.model.resp.BaseDetailResp;
import top.continew.admin.common.constant.ContainerConstants;
import top.continew.admin.review.common.enums.ProjectStatus;
import top.continew.admin.review.form.model.resp.FormTemplateResp;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 项目详情响应参数（申请人视角：申请信息 + 当前进度 + 阶段列表）
 *
 * @author zjx
 * @since 2026-03-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "项目详情响应参数")
public class ProjectDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

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
     * 项目类型ID
     */
    @Schema(description = "项目类型ID", example = "1737209001001")
    private Long typeId;

    /**
     * 项目类型名称（由 Service 层查询 review_project_type 手动填充）
     */
    @Schema(description = "项目类型名称", example = "科研项目")
    private String typeName;

    /**
     * 当前状态
     */
    @Schema(description = "当前状态", example = "EXECUTING")
    private ProjectStatus status;

    /**
     * 当前节点类型
     */
    @Schema(description = "当前节点类型", example = "EXECUTION")
    private String currentNodeType;

    /**
     * 当前节点序号
     */
    @Schema(description = "当前节点序号", example = "2")
    private Integer currentNodeSequence;

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
     * 申请表单填写数据（key=字段编码，value=字段值）
     */
    @Schema(description = "申请表单填写数据")
    private Map<String, Object> applicationFormData;

    /**
     * 管理阶段列表（决策通过后才有；含各阶段状态和成果表单数据）
     */
    @Schema(description = "管理阶段列表")
    private List<ProjectStageResp> stages;

    /**
     * 申请表单模板（供前端只读渲染字段结构）
     */
    @Schema(description = "申请表单模板")
    private FormTemplateResp applicationFormTemplate;

    /**
     * 评审阶段进度（从快照构建，含所有轮次及当前状态）
     */
    @Schema(description = "评审阶段进度")
    private List<NodeProgressItem> reviewProgress;

    /**
     * 管理阶段进度（从快照构建，含所有阶段及当前状态）
     */
    @Schema(description = "管理阶段进度")
    private List<StageProgressItem> stageProgress;

    @Data
    @Schema(description = "评审节点进度项")
    public static class NodeProgressItem {
        @Schema(description = "节点类型（AUDIT/REVIEW/DECISION）")
        private String nodeType;
        @Schema(description = "节点序号")
        private Integer nodeSequence;
        @Schema(description = "节点名称")
        private String nodeName;
        /** PENDING / ACTIVE / COMPLETED / REJECTED */
        @Schema(description = "节点状态")
        private String nodeStatus;
        @Schema(description = "通过人数（已完成节点才有）")
        private Integer passCount;
        @Schema(description = "总人数（已完成节点才有）")
        private Integer totalCount;
        @Schema(description = "平均分（SCORE_PASS 模式才有）")
        private java.math.BigDecimal averageScore;
        @Schema(description = "节点结果（PASS/REJECT，已完成节点才有）")
        private String nodeResult;
    }

    @Data
    @Schema(description = "管理阶段进度项")
    public static class StageProgressItem {
        @Schema(description = "阶段序号")
        private Integer stageOrder;
        @Schema(description = "阶段名称")
        private String stageName;
        @Schema(description = "阶段类型（KICKOFF/EXECUTION/ACCEPTANCE）")
        private String stageType;
        /** PENDING / IN_PROGRESS / SUBMITTED / COMPLETED / REJECTED */
        @Schema(description = "阶段状态")
        private String stageStatus;
        @Schema(description = "是否超期")
        private Boolean isOverdue;
    }
}
