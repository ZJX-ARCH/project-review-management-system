package top.continew.admin.review.form.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.continew.admin.common.base.model.entity.BaseDO;
import top.continew.admin.common.enums.DisEnableStatusEnum;
import top.continew.admin.review.form.enums.TemplateTypeEnum;

import java.io.Serial;

/**
 * 表单模板实体
 *
 * @author zjx
 * @since 2026-01-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "review_form_template", autoResultMap = true)
public class FormTemplateDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板编码（唯一）
     */
    private String templateCode;

    /**
     * 模板类型（1=申请,2=审核,3=评审,4=决策,5=立项阶段,6=执行阶段,7=验收阶段）
     */
    private TemplateTypeEnum templateType;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 模板描述
     */
    private String description;

    /**
     * 布局配置（JSON格式：gridCols、labelWidth、labelAlign）
     */
    private String layoutConfig;

    /**
     * 状态（1：启用；2：禁用）
     */
    private DisEnableStatusEnum status;

    /**
     * 排序
     */
    private Integer sort;
}
