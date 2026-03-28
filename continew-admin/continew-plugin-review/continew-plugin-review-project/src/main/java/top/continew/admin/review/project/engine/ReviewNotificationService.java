package top.continew.admin.review.project.engine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import top.continew.admin.review.common.enums.TaskType;
import top.continew.admin.system.enums.MessageTypeEnum;
import top.continew.admin.system.mapper.user.UserMapper;
import top.continew.admin.system.model.entity.user.UserDO;
import top.continew.admin.system.model.req.MessageReq;
import top.continew.admin.system.service.MessageService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 评审流程通知服务（异步发送站内消息）
 *
 * @author zjx
 * @since 2026-03-28
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewNotificationService {

    private final MessageService messageService;
    private final UserMapper userMapper;

    private static final Map<String, String> TASK_TYPE_LABEL = Map.of(
            "AUDIT", "审核",
            "REVIEW", "评审",
            "DECISION", "决策",
            "MANAGEMENT", "管理",
            "ACCEPTANCE", "验收",
            "STAGE_SUBMISSION", "阶段提交"
    );

    // ==================== 任务分配通知 ====================

    /**
     * 通知被分配任务的人员
     */
    @Async
    public void notifyTaskAssigned(Long projectId, String projectName, TaskType taskType,
                                    String nodeName, Collection<Long> assigneeIds) {
        String typeLabel = TASK_TYPE_LABEL.getOrDefault(taskType.getValue(), taskType.getValue());
        String title = "您有新的" + typeLabel + "任务";
        String content = "项目「" + projectName + "」的" + nodeName + "需要您处理，请及时查看。";
        sendToUsers(title, content, "/review/task", assigneeIds);
    }

    /**
     * 通知申请人提交阶段成果
     */
    @Async
    public void notifyStageSubmission(Long projectId, String projectName, String stageName, Long applicantId) {
        String title = "请提交阶段成果";
        String content = "项目「" + projectName + "」的「" + stageName + "」阶段已激活，请提交阶段成果。";
        sendToUser(title, content, "/review/task", applicantId);
    }

    // ==================== 评审阶段结果通知 ====================

    /**
     * 评审节点通过，通知申请人
     */
    @Async
    public void notifyReviewNodePassed(Long projectId, String projectName, TaskType taskType,
                                        Integer nodeSequence, Long applicantId) {
        String typeLabel = TASK_TYPE_LABEL.getOrDefault(taskType.getValue(), taskType.getValue());
        String title = typeLabel + "已通过";
        String content = "项目「" + projectName + "」的" + typeLabel + "第" + nodeSequence + "轮已通过。";
        sendToUser(title, content, "/review/project/detail/" + projectId, applicantId);
    }

    /**
     * 评审节点驳回，通知申请人
     */
    @Async
    public void notifyReviewNodeRejected(Long projectId, String projectName, TaskType taskType,
                                          Integer nodeSequence, Long applicantId) {
        String typeLabel = TASK_TYPE_LABEL.getOrDefault(taskType.getValue(), taskType.getValue());
        String title = typeLabel + "未通过";
        String content = "项目「" + projectName + "」在" + typeLabel + "第" + nodeSequence + "轮被驳回，项目已终止。";
        sendToUser(title, content, "/review/project/detail/" + projectId, applicantId);
    }

    /**
     * 所有评审轮次通过，进入执行阶段，通知申请人
     */
    @Async
    public void notifyEnterExecution(Long projectId, String projectName, Long applicantId) {
        String title = "评审通过，进入执行阶段";
        String content = "项目「" + projectName + "」已通过全部评审，进入执行阶段，请提交首个阶段成果。";
        sendToUser(title, content, "/review/task", applicantId);
    }

    // ==================== 管理阶段结果通知 ====================

    /**
     * 管理阶段通过，通知申请人
     */
    @Async
    public void notifyStagePassed(Long projectId, String projectName, String stageName, Long applicantId) {
        String title = "阶段审核通过";
        String content = "项目「" + projectName + "」的「" + stageName + "」阶段审核已通过。";
        sendToUser(title, content, "/review/project/detail/" + projectId, applicantId);
    }

    /**
     * 管理/验收阶段驳回，通知申请人
     */
    @Async
    public void notifyStageRejected(Long projectId, String projectName, String currentStageName,
                                     String backToStageName, Long applicantId) {
        String title = "阶段审核被驳回";
        String content = "项目「" + projectName + "」的「" + currentStageName + "」阶段被驳回，已回退到「"
                + backToStageName + "」阶段，请重新提交成果。";
        sendToUser(title, content, "/review/task", applicantId);
    }

    /**
     * 管理阶段评定为不合格，通知申请人
     */
    @Async
    public void notifyStageUnqualified(Long projectId, String projectName, String stageName, Long applicantId) {
        String title = "项目评定为不合格";
        String content = "项目「" + projectName + "」在「" + stageName + "」阶段被评定为不合格，项目已归档。";
        sendToUser(title, content, "/review/project/detail/" + projectId, applicantId);
    }

    /**
     * 管理阶段撤销，通知申请人
     */
    @Async
    public void notifyStageWithdrawn(Long projectId, String projectName, String stageName, Long applicantId) {
        String title = "项目已撤销";
        String content = "项目「" + projectName + "」在「" + stageName + "」阶段被撤销，项目已归档。";
        sendToUser(title, content, "/review/project/detail/" + projectId, applicantId);
    }

    // ==================== 验收结果通知 ====================

    /**
     * 验收通过，通知申请人
     */
    @Async
    public void notifyAcceptancePassed(Long projectId, String projectName, Long applicantId) {
        String title = "项目验收通过";
        String content = "项目「" + projectName + "」已通过验收，项目已归档完成。";
        sendToUser(title, content, "/review/project/detail/" + projectId, applicantId);
    }

    // ==================== 转办通知 ====================

    /**
     * 通知被转办人
     */
    @Async
    public void notifyTaskTransferred(Long projectId, String projectName, String fromUserName,
                                       String nodeName, Long targetUserId) {
        String title = "您收到一个转办任务";
        String content = fromUserName + " 将项目「" + projectName + "」的" + nodeName + "任务转办给您，请及时处理。";
        sendToUser(title, content, "/review/task", targetUserId);
    }

    /**
     * 通知申请人项目已被提交（确认收到）
     */
    @Async
    public void notifyProjectSubmitted(Long projectId, String projectName, Long applicantId) {
        String title = "项目已提交";
        String content = "项目「" + projectName + "」已成功提交，正在等待审核。";
        sendToUser(title, content, "/review/project/detail/" + projectId, applicantId);
    }

    // ==================== 内部方法 ====================

    private void sendToUser(String title, String content, String path, Long userId) {
        sendToUsers(title, content, path, List.of(userId));
    }

    private void sendToUsers(String title, String content, String path, Collection<Long> userIds) {
        try {
            MessageReq req = new MessageReq(MessageTypeEnum.SYSTEM);
            req.setTitle(title);
            req.setContent(content);
            req.setPath(path);
            List<String> userIdList = userIds.stream().map(String::valueOf).toList();
            messageService.add(req, userIdList);
            log.info("[Notification] 发送通知「{}」给用户 {}", title, userIdList);
        } catch (Exception e) {
            log.error("[Notification] 发送通知失败：title={}, users={}, error={}", title, userIds, e.getMessage());
        }
    }

    /**
     * 根据用户ID获取昵称
     */
    public String getNickname(Long userId) {
        if (userId == null) return "未知用户";
        UserDO user = userMapper.selectById(userId);
        return user != null ? user.getNickname() : "未知用户";
    }
}
