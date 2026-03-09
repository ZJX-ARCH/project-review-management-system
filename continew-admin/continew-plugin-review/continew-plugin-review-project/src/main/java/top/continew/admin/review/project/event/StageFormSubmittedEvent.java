package top.continew.admin.review.project.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 阶段成果表单提交事件（申请人提交阶段成果后发布，触发分配管理任务）
 *
 * @author zjx
 * @since 2026-03-07
 */
@Getter
public class StageFormSubmittedEvent extends ApplicationEvent {

    /** 项目ID */
    private final Long projectId;

    /** 阶段ID */
    private final Long stageId;

    /** 阶段序号（用于查询对应人员配置） */
    private final Integer stageOrder;

    public StageFormSubmittedEvent(Object source, Long projectId, Long stageId, Integer stageOrder) {
        super(source);
        this.projectId = projectId;
        this.stageId = stageId;
        this.stageOrder = stageOrder;
    }
}
