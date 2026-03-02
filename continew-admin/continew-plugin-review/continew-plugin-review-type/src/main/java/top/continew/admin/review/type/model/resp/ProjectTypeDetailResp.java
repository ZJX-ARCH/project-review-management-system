package top.continew.admin.review.type.model.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.continew.admin.common.base.model.resp.BaseDetailResp;
import top.continew.admin.review.type.enums.TypeStatusEnum;

import java.io.Serial;
import java.util.List;

/**
 * 项目类型完整配置详情响应参数
 *
 * @author zjx
 * @since 2026-03-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "项目类型完整配置详情响应参数")
public class ProjectTypeDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 类型名称
     */
    @Schema(description = "类型名称", example = "科研项目")
    private String typeName;

    /**
     * 类型编码
     */
    @Schema(description = "类型编码", example = "RESEARCH_PROJECT")
    private String typeCode;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String description;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

    /**
     * 状态（0=草稿；1=已启用；2=已禁用）
     */
    @Schema(description = "状态", example = "1")
    private TypeStatusEnum status;

    /**
     * 所属部门ID
     */
    @Schema(description = "所属部门ID", example = "1")
    private Long deptId;

    // ===== 子配置 =====

    /**
     * 流程配置（REVIEW + MANAGE 各一条）
     */
    @Schema(description = "流程配置列表")
    private List<TypeProcessConfigResp> processConfigs;

    /**
     * 表单映射列表（按流程节点展开）
     */
    @Schema(description = "表单映射列表")
    private List<TypeFormMappingResp> formMappings;

    /**
     * 人员范围配置（5类角色）
     */
    @Schema(description = "人员范围配置列表")
    private List<TypePersonnelConfigResp> personnelConfigs;

    /**
     * 审批规则配置（每个需判定节点一条）
     */
    @Schema(description = "审批规则配置列表")
    private List<TypeApprovalConfigResp> approvalConfigs;

    /**
     * 可见范围配置
     */
    @Schema(description = "可见范围配置列表")
    private List<TypeVisibilityConfigResp> visibilityConfigs;
}
