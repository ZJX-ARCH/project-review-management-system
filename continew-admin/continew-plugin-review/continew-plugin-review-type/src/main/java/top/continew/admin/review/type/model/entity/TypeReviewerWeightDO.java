package top.continew.admin.review.type.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.continew.admin.common.base.model.entity.BaseDO;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 评审人权重配置实体（PRESET 模式专用）
 *
 * @author zjx
 * @since 2026-03-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "review_type_reviewer_weight", autoResultMap = true)
public class TypeReviewerWeightDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目类型ID
     */
    private Long typeId;

    /**
     * 节点标识（与approval_config.node_scope对应）
     */
    private String nodeScope;

    /**
     * 评审人用户ID
     */
    private Long userId;

    /**
     * 评审人姓名（冗余）
     */
    private String userName;

    /**
     * 权重值（0~1，同一节点所有人权重之和须=1.0）
     */
    private BigDecimal weight;
}
