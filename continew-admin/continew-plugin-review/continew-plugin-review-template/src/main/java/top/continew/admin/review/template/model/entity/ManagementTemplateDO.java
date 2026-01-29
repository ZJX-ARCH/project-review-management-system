package top.continew.admin.review.template.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.continew.admin.common.base.model.entity.BaseDO;
import top.continew.admin.common.enums.DisEnableStatusEnum;

import java.io.Serial;
import java.util.List;

/**
 * 管理流程模板实体
 *
 * @author zjx
 * @since 2026-01-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "review_management_template", autoResultMap = true)
public class ManagementTemplateDO extends BaseDO {

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
     * 可见部门ID列表（JSON数组）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Long> deptIds;

    /**
     * 可见角色ID列表（JSON数组）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Long> roleIds;

    /**
     * 是否公开
     */
    private Boolean isPublic;

    /**
     * 状态
     */
    private DisEnableStatusEnum status;

    /**
     * 排序
     */
    private Integer sort;
}
