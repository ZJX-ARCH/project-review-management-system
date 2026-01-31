package top.continew.admin.review.form.model.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 表单模板文件响应参数
 *
 * @author zjx
 * @since 2026-01-31
 */
@Data
@Schema(description = "表单模板文件响应参数")
public class FormTemplateFileResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件记录ID
     */
    @Schema(description = "文件记录ID", example = "3001")
    private Long id;

    /**
     * 模板ID
     */
    @Schema(description = "模板ID", example = "1001")
    private Long templateId;

    /**
     * 字段ID
     */
    @Schema(description = "字段ID", example = "2001")
    private Long fieldId;

    /**
     * 文件ID
     */
    @Schema(description = "文件ID", example = "5001")
    private Long fileId;

    /**
     * 文件名称
     */
    @Schema(description = "文件名称", example = "科研项目申请书模板.docx")
    private String fileName;

    /**
     * 文件URL
     */
    @Schema(description = "文件URL", example = "/file/download/5001")
    private String fileUrl;

    /**
     * 文件大小(字节)
     */
    @Schema(description = "文件大小(字节)", example = "102400")
    private Long fileSize;

    /**
     * 文件类型
     */
    @Schema(description = "文件类型(TEMPLATE/EXAMPLE)", example = "TEMPLATE")
    private String fileType;

    /**
     * 文件说明
     */
    @Schema(description = "文件说明", example = "科研项目申请书标准模板")
    private String description;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "1")
    private Integer sort;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2026-01-31 10:00:00")
    private LocalDateTime createTime;
}
