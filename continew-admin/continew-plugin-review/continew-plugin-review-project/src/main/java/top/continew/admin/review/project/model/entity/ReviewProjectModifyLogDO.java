package top.continew.admin.review.project.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.continew.admin.common.base.model.entity.BaseDO;

import java.io.Serial;

/**
 * 申请表单修改痕迹日志表实体（仅追加写入，不更新）
 *
 * @author zjx
 * @since 2026-03-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "review_project_modify_log", autoResultMap = true)
public class ReviewProjectModifyLogDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 修改人用户ID（实际发起修改的人，可能是管理员代改，与 create_user 区分）
     */
    private Long modifiedBy;

    /**
     * 修改前申请表单数据（JSON）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object oldFormData;

    /**
     * 修改后申请表单数据（JSON）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object newFormData;

    /**
     * 修改原因（可选）
     */
    private String modifyReason;
}
