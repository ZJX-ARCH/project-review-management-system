package top.continew.admin.review.project.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import top.continew.admin.review.common.enums.TaskType;

/**
 * 任务完成事件（单个任务提交后发布，触发检查本节点是否所有任务都完成）
 *
 * @author zjx
 * @since 2026-03-07
 */
@Getter
public class TaskCompletedEvent extends ApplicationEvent {

    /** 项目ID */
    private final Long projectId;

    /** 任务类型 */
    private final TaskType taskType;

    /** 节点序号 */
    private final Integer nodeSequence;

    public TaskCompletedEvent(Object source, Long projectId, TaskType taskType, Integer nodeSequence) {
        super(source);
        this.projectId = projectId;
        this.taskType = taskType;
        this.nodeSequence = nodeSequence;
    }
}
