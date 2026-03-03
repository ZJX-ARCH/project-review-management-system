package top.continew.admin.review.type.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.continew.admin.common.base.model.entity.BaseDO;
import top.continew.admin.review.type.enums.ScopeTypeEnum;

import java.io.Serial;
import java.util.Map;

/**
 * 类型人员范围配置实体
 *
 * @author zjx
 * @since 2026-03-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "review_type_personnel_config", autoResultMap = true)
public class TypePersonnelConfigDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目类型ID
     */
    private Long typeId;

    /**
     * 节点类型（APPLICATION/AUDIT/REVIEW/DECISION/STAGE）
     */
    private String nodeType;

    /**
     * 节点序号（轮次/阶段顺序，APPLICATION为NULL）
     */
    private Integer nodeSequence;

    /**
     * 范围类型（USER/ROLE/DEPT/COMBINED）
     */
    private ScopeTypeEnum scopeType;

    /**
     * 范围配置（JSON，结构随scopeType变化）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> scopeConfig;

    /**
     * 备注说明
     */
    private String remark;
}
