package top.continew.admin.review.project.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * 提交项目申请请求参数（申请人填写完申请表单后提交）
 *
 * @author zjx
 * @since 2026-03-07
 */
@Data
@Schema(description = "提交项目申请请求参数")
public class ProjectSubmitReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 申请表单填写数据（key=字段编码，value=字段值）
     */
    @Schema(description = "申请表单填写数据（key=字段编码，value=字段值）")
    @NotNull(message = "申请表单数据不能为空")
    private Map<String, Object> formData;
}
