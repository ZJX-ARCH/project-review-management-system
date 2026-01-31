package top.continew.admin.review.form.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.continew.admin.common.base.model.entity.BaseDO;

import java.io.Serial;

/**
 * 表单模板文件实体
 *
 * @author zjx
 * @since 2026-01-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("review_form_template_file")
public class FormTemplateFileDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模板ID
     */
    private Long templateId;

    /**
     * 字段ID（FILE_TEMPLATE类型字段关联）
     */
    private Long fieldId;

    /**
     * 文件ID（关联sys_file表）
     */
    private Long fileId;

    /**
     * 文件类型（TEMPLATE=模板文件,EXAMPLE=示例文件）
     */
    private String fileType;

    /**
     * 文件说明
     */
    private String description;

    /**
     * 排序
     */
    private Integer sort;
}
