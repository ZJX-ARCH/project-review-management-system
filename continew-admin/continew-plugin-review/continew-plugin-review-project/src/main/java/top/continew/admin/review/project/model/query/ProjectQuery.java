package top.continew.admin.review.project.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.continew.admin.review.common.enums.ProjectStatus;

import java.io.Serial;
import java.io.Serializable;

/**
 * 项目列表查询条件
 *
 * @author zjx
 * @since 2026-03-07
 */
@Data
@Schema(description = "项目列表查询条件")
public class ProjectQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目名称（模糊搜索）
     */
    @Schema(description = "项目名称", example = "科研")
    private String projectName;

    /**
     * 项目类型ID
     */
    @Schema(description = "项目类型ID", example = "1737209001001")
    private Long typeId;

    /**
     * 项目状态
     */
    @Schema(description = "项目状态", example = "AUDITING")
    private ProjectStatus status;

    /**
     * 申请人用户ID（管理员视角查询某人的项目时使用）
     */
    @Schema(description = "申请人用户ID", example = "1")
    private Long applicantId;
}
