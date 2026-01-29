package top.continew.admin.review.template.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.continew.admin.common.base.model.entity.BaseDO;
import top.continew.admin.review.template.enums.StageType;

import java.io.Serial;

/**
 * 管理流程阶段实体
 *
 * @author zjx
 * @since 2026-01-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("review_management_stage")
public class ManagementStageDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 所属模板ID
     */
    private Long templateId;

    /**
     * 阶段名称
     */
    private String stageName;

    /**
     * 阶段类型（KICKOFF=立项，EXECUTION=执行，ACCEPTANCE=验收）
     */
    private StageType stageType;

    /**
     * 阶段顺序（从1开始）
     */
    private Integer stageOrder;

    /**
     * 是否必须（true=必须，false=可选）
     */
    private Boolean isRequired;
}
