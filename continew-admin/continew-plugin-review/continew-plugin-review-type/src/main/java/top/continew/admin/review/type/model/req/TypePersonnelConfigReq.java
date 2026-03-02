package top.continew.admin.review.type.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import top.continew.admin.review.type.enums.RoleTypeEnum;
import top.continew.admin.review.type.enums.ScopeTypeEnum;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * 类型人员范围配置请求参数（单条）
 *
 * @author zjx
 * @since 2026-03-02
 */
@Data
@Schema(description = "类型人员范围配置请求参数")
public class TypePersonnelConfigReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色类型（AUDITOR/REVIEWER/DECISION_MAKER/MANAGER/ACCEPTANCE_INSPECTOR）
     */
    @Schema(description = "角色类型", example = "AUDITOR")
    @NotNull(message = "角色类型不能为空")
    private RoleTypeEnum roleType;

    /**
     * 范围类型（USER/ROLE/DEPT/COMBINED）
     */
    @Schema(description = "范围类型", example = "DEPT")
    @NotNull(message = "范围类型不能为空")
    private ScopeTypeEnum scopeType;

    /**
     * 范围配置（JSON，结构随scopeType变化）
     * DEPT: {"deptIds":[10,20],"includeSub":true}
     * USER: {"userIds":[1001,1002]}
     * ROLE: {"businessRoles":["REVIEWER"]}
     * COMBINED: {"rule":"PROJECT_DEPT_LEADER"}
     */
    @Schema(description = "范围配置JSON", example = "{\"deptIds\":[1],\"includeSub\":false}")
    @NotNull(message = "范围配置不能为空")
    private Map<String, Object> scopeConfig;

    /**
     * 备注说明
     */
    @Schema(description = "备注说明", example = "科研处全体成员")
    @Length(max = 500, message = "备注长度不能超过 {max} 个字符")
    private String remark;
}
