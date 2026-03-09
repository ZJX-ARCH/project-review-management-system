package top.continew.admin.review.project.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 项目提交事件（申请人提交申请后发布，触发第一轮任务分配）
 *
 * @author zjx
 * @since 2026-03-07
 */
@Getter
public class ProjectSubmittedEvent extends ApplicationEvent {

    /** 项目ID */
    private final Long projectId;

    public ProjectSubmittedEvent(Object source, Long projectId) {
        super(source);
        this.projectId = projectId;
    }
}
