package top.continew.admin.review.type.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.continew.admin.common.base.model.entity.BaseDO;
import top.continew.admin.review.type.enums.TypeStatusEnum;

import java.io.Serial;

/**
 * 项目类型实体
 *
 * @author zjx
 * @since 2026-03-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "review_project_type", autoResultMap = true)
public class ProjectTypeDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 类型编码（唯一，创建后不可修改）
     */
    private String typeCode;

    /**
     * 描述
     */
    private String description;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态（0：草稿；1：已启用；2：已禁用）
     */
    private TypeStatusEnum status;

    /**
     * 所属部门ID（数据权限范围）
     */
    private Long deptId;
}
