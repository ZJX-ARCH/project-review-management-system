package top.continew.admin.review.project.engine;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.continew.admin.common.enums.DisEnableStatusEnum;
import top.continew.admin.review.common.enums.TaskType;
import top.continew.admin.review.project.mapper.ReviewProjectMapper;
import top.continew.admin.review.project.mapper.ReviewTaskMapper;
import top.continew.admin.review.project.model.entity.ProjectTypeSnapshot;
import top.continew.admin.review.project.model.entity.ReviewProjectDO;
import top.continew.admin.review.project.model.entity.ReviewTaskDO;
import top.continew.admin.review.project.enums.TaskStatusEnum;
import top.continew.admin.review.type.mapper.TypePersonnelConfigMapper;
import top.continew.admin.review.type.model.entity.TypePersonnelConfigDO;
import top.continew.admin.system.mapper.DeptMapper;
import top.continew.admin.system.mapper.UserRoleMapper;
import top.continew.admin.system.mapper.user.UserMapper;
import top.continew.admin.system.model.entity.DeptDO;
import top.continew.admin.system.model.entity.UserRoleDO;
import top.continew.admin.system.model.entity.user.UserDO;
import top.continew.starter.core.exception.BusinessException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 任务分配引擎（七步算法：范围展开 → 用户过滤 → 确定人数 → 负载均衡 → 随机抽取 → 防重 → 批量创建）
 *
 * @author zjx
 * @since 2026-03-07
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskAssignmentEngine {

    /** 同类型任务积压阈值（超过此数则进备选池） */
    private static final int MAX_PENDING_SAME_TYPE = 5;

    /** 管理/验收阶段默认分配人数 */
    private static final int DEFAULT_MANAGEMENT_COUNT = 1;

    private final ReviewProjectMapper projectMapper;
    private final ReviewTaskMapper taskMapper;
    private final TypePersonnelConfigMapper personnelConfigMapper;
    private final UserMapper userMapper;
    private final DeptMapper deptMapper;
    private final UserRoleMapper userRoleMapper;
    private final ObjectMapper objectMapper;

    /**
     * 为指定节点分配任务（七步算法）
     *
     * @param projectId    项目ID
     * @param taskType     任务类型
     * @param nodeSequence 节点序号（评审轮次序号 or 管理阶段序号）
     */
    public void assignTasks(Long projectId, TaskType taskType, Integer nodeSequence) {
        ReviewProjectDO project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException("项目不存在：" + projectId);
        }

        // Step 1: 转换为 personnel_config 查询 key
        String personnelNodeType;
        Integer personnelSeq;
        if (taskType == TaskType.MANAGEMENT || taskType == TaskType.ACCEPTANCE) {
            personnelNodeType = "STAGE";
            personnelSeq = nodeSequence;
        } else {
            personnelNodeType = taskType.getValue();
            personnelSeq = nodeSequence;
        }

        // Step 2: 查人员范围并展开为候选池
        QueryWrapper<TypePersonnelConfigDO> configWrapper = new QueryWrapper<>();
        configWrapper.eq("type_id", project.getTypeId())
                .eq("node_type", personnelNodeType)
                .eq("node_sequence", personnelSeq)
                .eq("deleted", 0);
        TypePersonnelConfigDO personnelConfig = personnelConfigMapper.selectOne(configWrapper);
        if (personnelConfig == null) {
            throw new BusinessException("未找到节点人员配置：typeId=" + project.getTypeId()
                    + ", nodeType=" + personnelNodeType + ", nodeSequence=" + personnelSeq);
        }

        Set<Long> candidatePool = expandScope(personnelConfig);
        if (candidatePool.isEmpty()) {
            throw new BusinessException("候选人员池为空，无法分配任务。节点：" + personnelNodeType + "_" + personnelSeq
                    + "，请检查类型人员范围配置");
        }

        // Step 3: 确定分配人数
        int requiredCount = resolveRequiredCount(project, taskType, nodeSequence);

        // Step 4: 负载均衡筛选
        List<Long> priorityPool = candidatePool.stream()
                .filter(uid -> taskMapper.countActiveTasks(uid, taskType.getValue()) < MAX_PENDING_SAME_TYPE)
                .collect(Collectors.toList());

        if (priorityPool.size() < requiredCount && candidatePool.size() > priorityPool.size()) {
            log.warn("[TaskAssignment] 项目{} 节点{}-{} 优先池不足，回退完整候选池。候选数={}, 优先数={}, 需求数={}",
                    projectId, taskType, nodeSequence, candidatePool.size(), priorityPool.size(), requiredCount);
        }

        // Step 5: 随机抽取
        List<Long> assignPool = priorityPool.size() >= requiredCount ? priorityPool : new ArrayList<>(candidatePool);
        Collections.shuffle(assignPool);
        List<Long> selected = assignPool.subList(0, Math.min(requiredCount, assignPool.size()));

        // Step 6: 防重校验（幂等保护）
        QueryWrapper<ReviewTaskDO> existWrapper = new QueryWrapper<>();
        existWrapper.eq("project_id", projectId)
                .eq("task_type", taskType.getValue())
                .eq("node_sequence", nodeSequence)
                .in("status", Arrays.asList(
                        TaskStatusEnum.PENDING.getValue(),
                        TaskStatusEnum.SAVED.getValue(),
                        TaskStatusEnum.COMPLETED.getValue()))
                .eq("deleted", 0);
        Set<Long> existingAssignees = taskMapper.selectList(existWrapper)
                .stream().map(ReviewTaskDO::getAssigneeId).collect(Collectors.toSet());

        List<Long> toAssign = selected.stream()
                .filter(uid -> !existingAssignees.contains(uid))
                .collect(Collectors.toList());

        if (toAssign.isEmpty()) {
            log.info("[TaskAssignment] 项目{} 节点{}-{} 所有候选人已有任务，跳过分配（幂等）",
                    projectId, taskType, nodeSequence);
            return;
        }

        // Step 7: 批量创建任务
        List<ReviewTaskDO> tasks = toAssign.stream().map(uid -> {
            ReviewTaskDO task = new ReviewTaskDO();
            task.setProjectId(projectId);
            task.setTaskType(taskType);
            task.setNodeSequence(nodeSequence);
            task.setAssigneeId(uid);
            task.setStatus(TaskStatusEnum.PENDING);
            task.setTransferCount(0);
            task.setAssignTime(LocalDateTime.now());
            return task;
        }).collect(Collectors.toList());
        taskMapper.insertBatch(tasks);

        log.info("[TaskAssignment] 项目{} 节点{}-{} 成功分配 {} 个任务", projectId, taskType, nodeSequence, tasks.size());
    }

    /**
     * 展开人员范围为候选用户ID集合（过滤禁用/已删除账号）
     */
    private Set<Long> expandScope(TypePersonnelConfigDO config) {
        Set<Long> rawIds = new HashSet<>();
        String scopeType = config.getScopeType() != null ? config.getScopeType().getValue() : "";
        Map<String, Object> scopeConfig = config.getScopeConfig() instanceof Map<?, ?>
                ? (Map<String, Object>) config.getScopeConfig() : new HashMap<>();

        switch (scopeType) {
            case "USER" -> {
                Object userIdsObj = scopeConfig.get("userIds");
                if (userIdsObj instanceof List<?> list) {
                    list.forEach(id -> {
                        if (id != null) {
                            try { rawIds.add(Long.parseLong(String.valueOf(id))); } catch (NumberFormatException ignored) {}
                        }
                    });
                }
            }
            case "DEPT" -> {
                Object deptIdsObj = scopeConfig.get("deptIds");
                boolean includeSub = Boolean.TRUE.equals(scopeConfig.get("includeSub"));
                if (deptIdsObj instanceof List<?> list) {
                    Set<Long> deptIds = expandDeptIds(list, includeSub);
                    if (!deptIds.isEmpty()) {
                        QueryWrapper<UserDO> userWrapper = new QueryWrapper<>();
                        userWrapper.in("dept_id", deptIds).eq("deleted", 0).select("id");
                        userMapper.selectList(userWrapper).stream().map(UserDO::getId).forEach(rawIds::add);
                    }
                }
            }
            case "ROLE" -> {
                // I1: 按角色展开候选人员（查询持有指定角色的所有用户）
                Object roleIdsObj = scopeConfig.get("roleIds");
                if (roleIdsObj instanceof List<?> list) {
                    List<Long> roleIds = new ArrayList<>();
                    list.forEach(id -> {
                        if (id != null) {
                            try { roleIds.add(Long.parseLong(String.valueOf(id))); } catch (NumberFormatException ignored) {}
                        }
                    });
                    if (!roleIds.isEmpty()) {
                        QueryWrapper<UserRoleDO> roleWrapper = new QueryWrapper<>();
                        roleWrapper.in("role_id", roleIds).select("user_id");
                        userRoleMapper.selectList(roleWrapper).stream()
                                .map(UserRoleDO::getUserId)
                                .filter(Objects::nonNull)
                                .forEach(rawIds::add);
                    }
                }
            }
            case "COMBINED" -> {
                Object rulesObj = scopeConfig.get("rules");
                if (rulesObj instanceof List<?> rules) {
                    for (Object ruleObj : rules) {
                        if (ruleObj instanceof Map<?, ?> ruleMap) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> rule = (Map<String, Object>) ruleMap;
                            rawIds.addAll(expandSingleRule(rule));
                        }
                    }
                }
            }
            default -> log.warn("[TaskAssignment] 未知 scopeType：{}", scopeType);
        }

        if (rawIds.isEmpty()) {
            return Collections.emptySet();
        }

        // 过滤禁用/已删除账号
        QueryWrapper<UserDO> filterWrapper = new QueryWrapper<>();
        filterWrapper.in("id", rawIds)
                .eq("deleted", 0)
                .eq("status", DisEnableStatusEnum.ENABLE.getValue())
                .select("id");
        return userMapper.selectList(filterWrapper).stream()
                .map(UserDO::getId).collect(Collectors.toSet());
    }

    /**
     * 展开单条 COMBINED 子规则（支持 USER / DEPT / ROLE）
     */
    private Set<Long> expandSingleRule(Map<String, Object> rule) {
        Set<Long> ids = new HashSet<>();
        String type = String.valueOf(rule.getOrDefault("scopeType", ""));
        @SuppressWarnings("unchecked")
        Map<String, Object> cfg = rule.get("scopeConfig") instanceof Map
                ? (Map<String, Object>) rule.get("scopeConfig") : new HashMap<>();
        if ("USER".equals(type)) {
            Object userIdsObj = cfg.get("userIds");
            if (userIdsObj instanceof List<?> list) {
                list.forEach(id -> {
                    try { ids.add(Long.parseLong(String.valueOf(id))); } catch (NumberFormatException ignored) {}
                });
            }
        } else if ("DEPT".equals(type)) {
            Object deptIdsObj = cfg.get("deptIds");
            boolean includeSub = Boolean.TRUE.equals(cfg.get("includeSub"));
            if (deptIdsObj instanceof List<?> list) {
                Set<Long> deptIds = expandDeptIds(list, includeSub);
                if (!deptIds.isEmpty()) {
                    QueryWrapper<UserDO> w = new QueryWrapper<>();
                    w.in("dept_id", deptIds).eq("deleted", 0).select("id");
                    userMapper.selectList(w).stream().map(UserDO::getId).forEach(ids::add);
                }
            }
        } else if ("ROLE".equals(type)) {
            Object roleIdsObj = cfg.get("roleIds");
            if (roleIdsObj instanceof List<?> list) {
                List<Long> roleIds = new ArrayList<>();
                list.forEach(id -> {
                    try { roleIds.add(Long.parseLong(String.valueOf(id))); } catch (NumberFormatException ignored) {}
                });
                if (!roleIds.isEmpty()) {
                    QueryWrapper<UserRoleDO> w = new QueryWrapper<>();
                    w.in("role_id", roleIds).select("user_id");
                    userRoleMapper.selectList(w).stream()
                            .map(UserRoleDO::getUserId)
                            .filter(Objects::nonNull)
                            .forEach(ids::add);
                }
            }
        }
        return ids;
    }

    /**
     * 展开部门ID（含子部门）
     */
    private Set<Long> expandDeptIds(List<?> deptIdList, boolean includeSub) {
        Set<Long> result = new HashSet<>();
        for (Object id : deptIdList) {
            if (id == null) continue;
            try {
                long deptId = Long.parseLong(String.valueOf(id));
                result.add(deptId);
                if (includeSub) {
                    QueryWrapper<DeptDO> subWrapper = new QueryWrapper<>();
                    subWrapper.apply("FIND_IN_SET({0}, ancestors)", deptId).eq("deleted", 0);
                    deptMapper.selectList(subWrapper).stream().map(DeptDO::getId).forEach(result::add);
                }
            } catch (NumberFormatException ignored) {}
        }
        return result;
    }

    /**
     * C5: 校验快照中所有节点的人员配置均充足（提交前检查，提前暴露配置缺陷）
     *
     * @param typeId   项目类型ID
     * @param snapshot 已构建的类型快照
     */
    public void validatePersonnelSufficiency(Long typeId, ProjectTypeSnapshot snapshot) {
        // 校验评审轮次
        if (snapshot.getRounds() != null) {
            for (ProjectTypeSnapshot.RoundInfo round : snapshot.getRounds()) {
                TaskType taskType = TaskType.valueOf(round.getRoundType());
                Set<Long> candidates = findCandidates(typeId, taskType, round.getRoundSequence());
                if (candidates.isEmpty()) {
                    throw new top.continew.starter.core.exception.BusinessException(
                            "节点 " + round.getRoundType() + "_" + round.getRoundSequence()
                                    + "（" + round.getRoundName() + "）人员范围为空，请先完成人员配置再提交申请");
                }
            }
        }
        // 校验执行阶段
        if (snapshot.getStages() != null) {
            for (ProjectTypeSnapshot.StageInfo stage : snapshot.getStages()) {
                TaskType taskType = TaskType.valueOf(stage.getStageType());
                Set<Long> candidates = findCandidates(typeId, taskType, stage.getStageOrder());
                if (candidates.isEmpty()) {
                    throw new top.continew.starter.core.exception.BusinessException(
                            "阶段「" + stage.getStageName() + "」人员范围为空，请先完成人员配置再提交申请");
                }
            }
        }
    }

    /**
     * 查询某节点的候选用户集合（不做任务创建，供校验和转办使用）
     */
    public Set<Long> findCandidates(Long typeId, TaskType taskType, Integer nodeSequence) {
        String personnelNodeType = (taskType == TaskType.MANAGEMENT || taskType == TaskType.ACCEPTANCE)
                ? "STAGE" : taskType.getValue();
        QueryWrapper<TypePersonnelConfigDO> configWrapper = new QueryWrapper<>();
        configWrapper.eq("type_id", typeId)
                .eq("node_type", personnelNodeType)
                .eq("node_sequence", nodeSequence)
                .eq("deleted", 0);
        TypePersonnelConfigDO personnelConfig = personnelConfigMapper.selectOne(configWrapper);
        if (personnelConfig == null) {
            return Collections.emptySet();
        }
        return expandScope(personnelConfig);
    }

    /**
     * 从快照读取分配人数（管理/验收阶段默认1人）
     */
    private int resolveRequiredCount(ReviewProjectDO project, TaskType taskType, Integer nodeSequence) {
        if (taskType == TaskType.MANAGEMENT || taskType == TaskType.ACCEPTANCE) {
            return DEFAULT_MANAGEMENT_COUNT;
        }
        // 评审阶段：从快照读 approvalRules[nodeScope].requiredReviewerCount
        try {
            ProjectTypeSnapshot snapshot = objectMapper.convertValue(project.getSnapshotConfig(), ProjectTypeSnapshot.class);
            String nodeScope = taskType.getValue() + "_" + nodeSequence;
            ProjectTypeSnapshot.ApprovalRuleInfo rule = snapshot.getApprovalRules().get(nodeScope);
            if (rule != null && rule.getRequiredReviewerCount() != null) {
                return rule.getRequiredReviewerCount();
            }
        } catch (Exception e) {
            log.warn("[TaskAssignment] 读取快照失败，使用默认人数1：{}", e.getMessage());
        }
        return DEFAULT_MANAGEMENT_COUNT;
    }
}
