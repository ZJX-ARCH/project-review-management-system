package top.continew.admin.review.type.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.continew.admin.common.base.model.entity.BaseDO;
import top.continew.admin.review.type.enums.VisibilityTypeEnum;

import java.io.Serial;

/**
 * 类型可见范围配置实体
 *
 * @author zjx
 * @since 2026-03-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "review_type_visibility_config", autoResultMap = true)
public class TypeVisibilityConfigDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目类型ID
     */
    private Long typeId;

    /**
     * 可见类型（ALL=全部；DEPT=指定部门；USER=指定用户）
     */
    private VisibilityTypeEnum visibilityType;

    /**
     * 目标ID（部门ID或用户ID，ALL类型时为NULL）
     */
    private Long targetId;

    /**
     * 目标名称（冗余）
     */
    private String targetName;
}
