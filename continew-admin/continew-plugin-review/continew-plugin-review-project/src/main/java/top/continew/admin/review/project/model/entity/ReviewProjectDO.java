package top.continew.admin.review.project.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.continew.admin.common.base.model.entity.BaseDO;
import top.continew.admin.review.common.enums.ProjectStatus;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 项目实例主表实体
 *
 * @author zjx
 * @since 2026-03-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "review_project", autoResultMap = true)
public class ReviewProjectDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目类型ID
     */
    private Long typeId;

    /**
     * 申请人所属部门ID
     */
    private Long deptId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 当前状态（对应 ProjectStatus 枚举）
     */
    private ProjectStatus status;

    /**
     * 当前节点类型（AUDIT/REVIEW/DECISION/KICKOFF/EXECUTION/ACCEPTANCE）
     */
    private String currentNodeType;

    /**
     * 当前节点序号
     */
    private Integer currentNodeSequence;

    /**
     * 提交时的类型配置快照（JSON，包含轮次/阶段结构、表单映射、审批规则）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object snapshotConfig;

    /**
     * 申请表单填写数据（JSON）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object applicationFormData;

    /**
     * 申请人用户ID
     */
    private Long applicantId;

    /**
     * 提交时间（从 DRAFT 变为 SUBMITTED 时记录）
     */
    private LocalDateTime submittedTime;
}
