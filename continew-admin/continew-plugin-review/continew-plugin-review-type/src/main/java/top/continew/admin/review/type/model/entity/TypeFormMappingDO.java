package top.continew.admin.review.type.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.continew.admin.common.base.model.entity.BaseDO;
import top.continew.admin.review.type.enums.NodeTypeEnum;
import top.continew.admin.review.type.enums.ProcessTypeEnum;

import java.io.Serial;

/**
 * 类型表单映射实体
 *
 * @author zjx
 * @since 2026-03-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "review_type_form_mapping", autoResultMap = true)
public class TypeFormMappingDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目类型ID
     */
    private Long typeId;

    /**
     * 映射类型（REVIEW=评审流程；MANAGE=管理流程）
     */
    private ProcessTypeEnum mappingType;

    /**
     * 节点类型（APPLICATION/AUDIT/REVIEW/DECISION/STAGE）
     */
    private NodeTypeEnum nodeType;

    /**
     * 节点序号（APPLICATION节点为NULL，其他节点从1开始与流程轮次/阶段序号对应）
     */
    private Integer nodeSequence;

    /**
     * 表单模板ID（逻辑外键）
     */
    private Long formTemplateId;

    /**
     * 表单模板名称（冗余）
     */
    private String formTemplateName;
}
