package top.continew.admin.review.project.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import top.continew.admin.review.common.enums.TaskType;
import top.continew.admin.review.project.enums.TaskDecisionEnum;

/**
 * 节点汇总结果事件（结果引擎汇总完成后发布，触发流程推进或终止）
 *
 * @author zjx
 * @since 2026-03-07
 */
@Getter
public class NodeResultEvent extends ApplicationEvent {

    /** 项目ID */
    private final Long projectId;

    /** 节点任务类型 */
    private final TaskType taskType;

    /** 节点序号 */
    private final Integer nodeSequence;

    /** 汇总结果（PASS/REJECT） */
    private final TaskDecisionEnum result;

    /**
     * 验收或管理阶段 REJECT 时指定的回退阶段序号（ACCEPTANCE/MANAGEMENT + REJECT 时有效）
     */
    private final Integer rejectBackToStageOrder;

    public NodeResultEvent(Object source, Long projectId, TaskType taskType,
                           Integer nodeSequence, TaskDecisionEnum result,
                           Integer rejectBackToStageOrder) {
        super(source);
        this.projectId = projectId;
        this.taskType = taskType;
        this.nodeSequence = nodeSequence;
        this.result = result;
        this.rejectBackToStageOrder = rejectBackToStageOrder;
    }
}
