package top.continew.admin.review.template.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.continew.admin.common.base.model.entity.BaseDO;
import top.continew.admin.review.template.enums.RoundType;

import java.io.Serial;

/**
 * 流程模板轮次名称实体
 *
 * @author zjx
 * @since 2026-01-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("review_process_template_round_name")
public class ProcessTemplateRoundNameDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 流程模板ID
     */
    private Long templateId;

    /**
     * 轮次类型（AUDIT=审核,REVIEW=评审,DECISION=决策）
     */
    private RoundType roundType;

    /**
     * 轮次序号（第几轮，从1开始）
     */
    private Integer roundSequence;

    /**
     * 轮次名称
     */
    private String roundName;
}
