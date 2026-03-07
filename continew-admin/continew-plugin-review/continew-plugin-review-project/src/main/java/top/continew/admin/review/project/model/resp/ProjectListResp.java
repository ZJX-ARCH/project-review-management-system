package top.continew.admin.review.project.model.resp;

import cn.crane4j.annotation.Assemble;
import cn.crane4j.annotation.Mapping;
import cn.crane4j.annotation.condition.ConditionOnPropertyNotNull;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.continew.admin.common.base.model.resp.BaseDetailResp;
import top.continew.admin.common.constant.ContainerConstants;
import top.continew.admin.review.common.enums.ProjectStatus;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 项目列表响应参数
 *
 * @author zjx
 * @since 2026-03-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ExcelIgnoreUnannotated
@Schema(description = "项目列表响应参数")
public class ProjectListResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称", example = "基于深度学习的图像识别研究")
    @ExcelProperty(value = "项目名称", order = 2)
    private String projectName;

    /**
     * 项目类型ID
     */
    @Schema(description = "项目类型ID", example = "1737209001001")
    private Long typeId;

    /**
     * 项目类型名称（由 Service 层查询 review_project_type 手动填充）
     */
    @Schema(description = "项目类型名称", example = "科研项目")
    @ExcelProperty(value = "项目类型", order = 3)
    private String typeName;

    /**
     * 当前状态
     */
    @Schema(description = "当前状态", example = "AUDITING")
    @ExcelProperty(value = "状态", order = 4)
    private ProjectStatus status;

    /**
     * 申请人用户ID
     */
    @JsonIgnore
    @ConditionOnPropertyNotNull
    @Assemble(container = ContainerConstants.USER_NICKNAME, props = @Mapping(ref = "applicantName"))
    private Long applicantId;

    /**
     * 申请人姓名
     */
    @Schema(description = "申请人姓名", example = "张三")
    @ExcelProperty(value = "申请人", order = 5)
    private String applicantName;

    /**
     * 提交时间
     */
    @Schema(description = "提交时间", example = "2026-03-07 10:00:00")
    @ExcelProperty(value = "提交时间", order = 6)
    private LocalDateTime submittedTime;

    /**
     * 当前节点类型（展示当前处于哪个审核环节）
     */
    @Schema(description = "当前节点类型", example = "AUDIT")
    private String currentNodeType;

    /**
     * 当前节点序号
     */
    @Schema(description = "当前节点序号", example = "1")
    private Integer currentNodeSequence;
}
