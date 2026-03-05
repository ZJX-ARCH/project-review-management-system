package top.continew.admin.review.type.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 统计人员范围人数请求参数
 *
 * @author zjx
 * @since 2026-03-05
 */
@Data
@Schema(description = "统计人员范围人数请求参数")
public class CountScopeReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色ID（null 时不按角色过滤）
     */
    @Schema(description = "角色ID（null 时不按角色过滤）")
    private Long roleId;

    /**
     * 范围规则列表（并集统计）
     */
    @Schema(description = "范围规则列表（并集统计）")
    private List<ScopeRuleItem> rules;

    /**
     * 单条范围规则
     */
    @Data
    @Schema(description = "单条范围规则")
    public static class ScopeRuleItem {

        /**
         * 范围类型（USER / DEPT）
         */
        @Schema(description = "范围类型（USER/DEPT）", example = "USER")
        private String scopeType;

        /**
         * 范围配置（与 TypePersonnelConfigReq.scopeConfig 格式一致）
         */
        @Schema(description = "范围配置，USER：{userIds:[1,2]}，DEPT：{deptIds:[1,2],includeSub:true}")
        private Map<String, Object> scopeConfig;
    }
}
