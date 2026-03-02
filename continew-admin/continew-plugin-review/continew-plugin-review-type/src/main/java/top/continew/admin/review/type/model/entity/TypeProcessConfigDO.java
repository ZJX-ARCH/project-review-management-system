package top.continew.admin.review.type.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.continew.admin.common.base.model.entity.BaseDO;
import top.continew.admin.review.type.enums.ProcessTypeEnum;

import java.io.Serial;

/**
 * 类型流程配置实体
 *
 * @author zjx
 * @since 2026-03-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "review_type_process_config", autoResultMap = true)
public class TypeProcessConfigDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目类型ID
     */
    private Long typeId;

    /**
     * 流程类型（REVIEW=评审流程；MANAGE=管理流程）
     */
    private ProcessTypeEnum processType;

    /**
     * 流程模板ID（逻辑外键）
     */
    private Long templateId;

    /**
     * 流程模板名称（冗余，便于查询展示）
     */
    private String templateName;

    /**
     * 是否主流程
     */
    private Boolean isPrimary;
}
