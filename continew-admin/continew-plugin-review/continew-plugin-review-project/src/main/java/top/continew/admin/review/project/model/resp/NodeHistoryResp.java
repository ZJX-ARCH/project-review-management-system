package top.continew.admin.review.project.model.resp;

import lombok.Data;
import top.continew.admin.review.form.model.resp.FormTemplateResp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 节点历史响应（含每人填写内容）
 *
 * @author zjx
 * @since 2026-03-25
 */
@Data
public class NodeHistoryResp implements Serializable {

    /** 节点类型 APPLICATION/AUDIT/REVIEW/DECISION */
    private String nodeType;

    /** 节点序号（申请表单为 0） */
    private Integer nodeSequence;

    /** 节点名称 */
    private String nodeName;

    /** 节点汇总结果 PASS/REJECT（已完成节点才有） */
    private String nodeResult;

    /** 通过人数 */
    private Integer passCount;

    /** 总人数 */
    private Integer totalCount;

    /** 平均分（SCORE_PASS 模式） */
    private BigDecimal averageScore;

    /** 该节点每个人的填写记录 */
    private List<PersonEntryResp> entries;

    /**
     * 每人填写记录
     */
    @Data
    public static class PersonEntryResp implements Serializable {

        /** 处理人姓名 */
        private String assigneeName;

        /** 决策结果 PASS/REJECT */
        private String decision;

        /** 评分（SCORE_PASS 模式） */
        private BigDecimal score;

        /** 表单填写数据 */
        private Map<String, Object> formData;

        /** 表单模板（用于渲染） */
        private FormTemplateResp formTemplate;

        /** 完成时间 */
        private LocalDateTime completeTime;
    }
}
