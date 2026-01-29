package top.continew.admin.review.template.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.continew.admin.common.base.model.entity.BaseDO;
import top.continew.admin.common.enums.DisEnableStatusEnum;

import java.io.Serial;

/**
 * 评审流程模板实体
 *
 * @author zjx
 * @since 2026-01-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "review_process_template", autoResultMap = true)
public class ProcessTemplateDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板编码
     */
    private String templateCode;

    /**
     * 模板描述
     */
    private String description;

    /**
     * 审核轮次（0-10，0表示跳过）
     */
    private Integer auditRounds;

    /**
     * 评审轮次（0-10，0表示跳过）
     */
    private Integer reviewRounds;

    /**
     * 决策轮次（1-10，至少1轮）
     */
    private Integer decisionRounds;

    /**
     * 状态
     */
    private DisEnableStatusEnum status;

    /**
     * 排序
     */
    private Integer sort;
}
