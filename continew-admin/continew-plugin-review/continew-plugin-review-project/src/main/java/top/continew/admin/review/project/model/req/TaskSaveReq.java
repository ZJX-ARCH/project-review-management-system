package top.continew.admin.review.project.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * 暂存任务请求参数（保存填写进度，不触发流程推进）
 *
 * @author zjx
 * @since 2026-03-07
 */
@Data
@Schema(description = "暂存任务请求参数")
public class TaskSaveReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务表单填写数据（key=字段编码，value=字段值；允许为空，表示清空暂存）
     */
    @Schema(description = "任务表单填写数据（key=字段编码，value=字段值）")
    private Map<String, Object> formData;
}
