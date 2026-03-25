package top.continew.admin.review.project.engine;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.continew.admin.common.enums.DisEnableStatusEnum;
import top.continew.admin.review.common.enums.TaskType;
import top.continew.admin.review.project.enums.TaskStatusEnum;
import top.continew.admin.review.project.mapper.ReviewProjectMapper;
import top.continew.admin.review.project.mapper.ReviewTaskMapper;
import top.continew.admin.review.project.model.entity.ProjectTypeSnapshot;
import top.continew.admin.review.project.model.entity.ReviewProjectDO;
import top.continew.admin.review.project.model.entity.ReviewTaskDO;
import top.continew.admin.review.type.mapper.TypePersonnelConfigMapper;
import top.continew.admin.review.type.model.entity.TypePersonnelConfigDO;
import top.continew.admin.system.mapper.DeptMapper;
import top.continew.admin.system.mapper.RoleMapper;
import top.continew.admin.system.mapper.UserRoleMapper;
import top.continew.admin.system.mapper.user.UserMapper;
import top.continew.admin.system.model.entity.DeptDO;
import top.continew.admin.system.model.entity.RoleDO;
import top.continew.admin.system.model.entity.UserRoleDO;
import top.continew.admin.system.model.entity.user.UserDO;
import top.continew.starter.core.exception.BusinessException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskAssignmentEngine {

    private static final int MAX_PENDING_SAME_TYPE = 5;
    private static final int DEFAULT_MANAGEMENT_COUNT = 1;

    private final ReviewProjectMapper projectMapper;
    private final ReviewTaskMapper taskMapper;
    private final TypePersonnelConfigMapper personnelConfigMapper;
    private final UserMapper userMapper;
    private final DeptMapper deptMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final ObjectMapper objectMapper;

    public void assignTasks(Long projectId, TaskType taskType, Integer nodeSequence) {
        ReviewProjectDO project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException("Project not found: " + projectId);
        }

        String personnelNodeType = resolvePersonnelNodeType(taskType);
        List<TypePersonnelConfigDO> personnelConfigs = listPersonnelConfigs(project.getTypeId(), personnelNodeType, nodeSequence);
        if (personnelConfigs.isEmpty()) {
            throw new BusinessException("Personnel config not found: typeId=" + project.getTypeId()
                    + ", nodeType=" + personnelNodeType + ", nodeSequence=" + nodeSequence);
        }

        Set<Long> candidatePool = applyNodeRoleFilter(
                collectScopedCandidates(personnelConfigs),
                resolveNodeRoleCode(taskType, personnelNodeType)
        );
        if (candidatePool.isEmpty()) {
            throw new BusinessException("No eligible assignees for node: " + buildNodeScope(taskType, nodeSequence));
        }

        int requiredCount = resolveRequiredCount(project, taskType, nodeSequence);
        if (candidatePool.size() < requiredCount) {
            throw new BusinessException("Insufficient eligible assignees for node " + buildNodeScope(taskType, nodeSequence)
                    + ": required=" + requiredCount + ", actual=" + candidatePool.size());
        }

        List<Long> priorityPool = candidatePool.stream()
                .filter(uid -> taskMapper.countActiveTasks(uid, taskType.getValue()) < MAX_PENDING_SAME_TYPE)
                .collect(Collectors.toList());
        if (priorityPool.size() < requiredCount && candidatePool.size() > priorityPool.size()) {
            log.warn("[TaskAssignment] project={} node={}-{} priorityPool insufficient, fallback to full pool. all={}, priority={}, required={}",
                    projectId, taskType, nodeSequence, candidatePool.size(), priorityPool.size(), requiredCount);
        }

        List<Long> assignPool = priorityPool.size() >= requiredCount ? priorityPool : new ArrayList<>(candidatePool);
        Collections.shuffle(assignPool);
        List<Long> selected = assignPool.subList(0, requiredCount);

        QueryWrapper<ReviewTaskDO> existWrapper = new QueryWrapper<>();
        existWrapper.eq("project_id", projectId)
                .eq("task_type", taskType.getValue())
                .eq("node_sequence", nodeSequence)
                .in("status", Arrays.asList(
                        TaskStatusEnum.PENDING.getValue(),
                        TaskStatusEnum.SAVED.getValue(),
                        TaskStatusEnum.COMPLETED.getValue()))
                .eq("deleted", 0);
        Set<Long> existingAssignees = taskMapper.selectList(existWrapper).stream()
                .map(ReviewTaskDO::getAssigneeId)
                .collect(Collectors.toSet());

        List<Long> toAssign = selected.stream()
                .filter(uid -> !existingAssignees.contains(uid))
                .collect(Collectors.toList());
        if (toAssign.isEmpty()) {
            log.info("[TaskAssignment] project={} node={}-{} all selected users already have tasks", projectId, taskType, nodeSequence);
            return;
        }

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

        log.info("[TaskAssignment] project={} node={}-{} assigned {} tasks", projectId, taskType, nodeSequence, tasks.size());
    }

    public void validatePersonnelSufficiency(Long typeId, ProjectTypeSnapshot snapshot) {
        if (snapshot.getRounds() != null) {
            for (ProjectTypeSnapshot.RoundInfo round : snapshot.getRounds()) {
                TaskType taskType = TaskType.valueOf(round.getRoundType());
                Set<Long> candidates = findCandidates(typeId, taskType, round.getRoundSequence());
                int requiredCount = resolveRequiredCount(snapshot, taskType, round.getRoundSequence());
                if (candidates.size() < requiredCount) {
                    throw new BusinessException("Insufficient eligible users for node "
                            + round.getRoundType() + "_" + round.getRoundSequence()
                            + ": required=" + requiredCount + ", actual=" + candidates.size());
                }
            }
        }

        if (snapshot.getStages() != null) {
            for (ProjectTypeSnapshot.StageInfo stage : snapshot.getStages()) {
                TaskType taskType = stageTypeToTaskType(stage.getStageType());
                Set<Long> candidates = findCandidates(typeId, taskType, stage.getStageOrder());
                int requiredCount = resolveRequiredCount(snapshot, taskType, stage.getStageOrder());
                if (candidates.size() < requiredCount) {
                    throw new BusinessException("Insufficient eligible users for stage "
                            + stage.getStageName() + ": required=" + requiredCount + ", actual=" + candidates.size());
                }
            }
        }
    }

    public void ensureApplicantEligible(Long typeId, Long userId) {
        Set<Long> applicantPool = findApplicationCandidates(typeId);
        if (!applicantPool.contains(userId)) {
            throw new BusinessException("Current user is not eligible to apply for this project type");
        }
    }

    public Set<Long> findCandidates(Long typeId, TaskType taskType, Integer nodeSequence) {
        String personnelNodeType = resolvePersonnelNodeType(taskType);
        List<TypePersonnelConfigDO> personnelConfigs = listPersonnelConfigs(typeId, personnelNodeType, nodeSequence);
        if (personnelConfigs.isEmpty()) {
            return Collections.emptySet();
        }
        return applyNodeRoleFilter(collectScopedCandidates(personnelConfigs), resolveNodeRoleCode(taskType, personnelNodeType));
    }

    public Set<Long> findApplicationCandidates(Long typeId) {
        List<TypePersonnelConfigDO> personnelConfigs = listPersonnelConfigs(typeId, "APPLICATION", null);
        if (personnelConfigs.isEmpty()) {
            return Collections.emptySet();
        }
        return applyNodeRoleFilter(collectScopedCandidates(personnelConfigs), "APPLICANT");
    }

    private Set<Long> expandScope(TypePersonnelConfigDO config) {
        Set<Long> rawIds = new HashSet<>();
        String scopeType = config.getScopeType() != null ? config.getScopeType().getValue() : "";
        @SuppressWarnings("unchecked")
        Map<String, Object> scopeConfig = config.getScopeConfig() instanceof Map<?, ?>
                ? (Map<String, Object>) config.getScopeConfig()
                : new HashMap<>();

        switch (scopeType) {
            case "USER" -> {
                Object userIdsObj = scopeConfig.get("userIds");
                if (userIdsObj instanceof List<?> list) {
                    list.forEach(id -> addLong(rawIds, id));
                }
            }
            case "DEPT" -> {
                Object deptIdsObj = scopeConfig.get("deptIds");
                boolean includeSub = Boolean.TRUE.equals(scopeConfig.get("includeSub"));
                if (deptIdsObj instanceof List<?> list) {
                    Set<Long> deptIds = expandDeptIds(list, includeSub);
                    if (!deptIds.isEmpty()) {
                        rawIds.addAll(userMapper.selectEnabledIdsByDeptIds(deptIds));
                    }
                }
            }
            case "ROLE" -> {
                Object roleIdsObj = scopeConfig.get("roleIds");
                if (roleIdsObj instanceof List<?> list) {
                    List<Long> roleIds = new ArrayList<>();
                    list.forEach(id -> addLong(roleIds, id));
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
            default -> log.warn("[TaskAssignment] unknown scopeType={}", scopeType);
        }

        if (rawIds.isEmpty()) {
            return Collections.emptySet();
        }
        return userMapper.selectBatchIds(rawIds).stream()
                .filter(u -> DisEnableStatusEnum.ENABLE.equals(u.getStatus()))
                .map(UserDO::getId)
                .collect(Collectors.toSet());
    }

    private Set<Long> expandSingleRule(Map<String, Object> rule) {
        Set<Long> ids = new HashSet<>();
        String type = String.valueOf(rule.getOrDefault("scopeType", ""));
        @SuppressWarnings("unchecked")
        Map<String, Object> cfg = rule.get("scopeConfig") instanceof Map
                ? (Map<String, Object>) rule.get("scopeConfig")
                : new HashMap<>();
        if ("USER".equals(type)) {
            Object userIdsObj = cfg.get("userIds");
            if (userIdsObj instanceof List<?> list) {
                list.forEach(id -> addLong(ids, id));
            }
        } else if ("DEPT".equals(type)) {
            Object deptIdsObj = cfg.get("deptIds");
            boolean includeSub = Boolean.TRUE.equals(cfg.get("includeSub"));
            if (deptIdsObj instanceof List<?> list) {
                Set<Long> deptIds = expandDeptIds(list, includeSub);
                if (!deptIds.isEmpty()) {
                    userMapper.selectEnabledIdsByDeptIds(deptIds).forEach(ids::add);
                }
            }
        } else if ("ROLE".equals(type)) {
            Object roleIdsObj = cfg.get("roleIds");
            if (roleIdsObj instanceof List<?> list) {
                List<Long> roleIds = new ArrayList<>();
                list.forEach(id -> addLong(roleIds, id));
                if (!roleIds.isEmpty()) {
                    QueryWrapper<UserRoleDO> wrapper = new QueryWrapper<>();
                    wrapper.in("role_id", roleIds).select("user_id");
                    userRoleMapper.selectList(wrapper).stream()
                            .map(UserRoleDO::getUserId)
                            .filter(Objects::nonNull)
                            .forEach(ids::add);
                }
            }
        }
        return ids;
    }

    private Set<Long> expandDeptIds(List<?> deptIdList, boolean includeSub) {
        Set<Long> result = new HashSet<>();
        for (Object id : deptIdList) {
            if (id == null) {
                continue;
            }
            try {
                long deptId = Long.parseLong(String.valueOf(id));
                result.add(deptId);
                if (includeSub) {
                    QueryWrapper<DeptDO> subWrapper = new QueryWrapper<>();
                    subWrapper.apply("FIND_IN_SET({0}, ancestors)", deptId).eq("deleted", 0);
                    deptMapper.selectList(subWrapper).stream().map(DeptDO::getId).forEach(result::add);
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return result;
    }

    private List<TypePersonnelConfigDO> listPersonnelConfigs(Long typeId, String nodeType, Integer nodeSequence) {
        QueryWrapper<TypePersonnelConfigDO> configWrapper = new QueryWrapper<>();
        configWrapper.eq("type_id", typeId)
                .eq("node_type", nodeType)
                .eq("deleted", 0);
        if (nodeSequence == null) {
            configWrapper.isNull("node_sequence");
        } else {
            configWrapper.eq("node_sequence", nodeSequence);
        }
        return personnelConfigMapper.selectList(configWrapper);
    }

    private Set<Long> collectScopedCandidates(List<TypePersonnelConfigDO> personnelConfigs) {
        Set<Long> candidates = new HashSet<>();
        for (TypePersonnelConfigDO cfg : personnelConfigs) {
            candidates.addAll(expandScope(cfg));
        }
        return candidates;
    }

    private Set<Long> applyNodeRoleFilter(Set<Long> candidatePool, String roleCode) {
        if (candidatePool.isEmpty() || roleCode == null || roleCode.isBlank()) {
            return candidatePool;
        }

        Long roleId = findRoleIdByCode(roleCode);
        if (roleId == null) {
            // 角色不存在时不过滤，直接使用范围配置的候选人
            log.warn("[TaskAssignment] roleCode={} not found, skip role filter", roleCode);
            return candidatePool;
        }

        QueryWrapper<UserRoleDO> roleWrapper = new QueryWrapper<>();
        roleWrapper.eq("role_id", roleId).select("user_id");
        Set<Long> roleUserIds = userRoleMapper.selectList(roleWrapper).stream()
                .map(UserRoleDO::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (roleUserIds.isEmpty()) {
            // 角色无成员时不过滤，兜底使用范围配置的候选人
            log.warn("[TaskAssignment] roleCode={} has no members, skip role filter", roleCode);
            return candidatePool;
        }

        Set<Long> filtered = new HashSet<>(candidatePool);
        filtered.retainAll(roleUserIds);
        if (filtered.isEmpty()) {
            // 交集为空时兜底使用范围配置的候选人，避免任务分配失败
            log.warn("[TaskAssignment] roleCode={} filter result empty, fallback to scope candidates", roleCode);
            return candidatePool;
        }
        return filtered;
    }

    private Long findRoleIdByCode(String roleCode) {
        QueryWrapper<RoleDO> wrapper = new QueryWrapper<>();
        wrapper.eq("code", roleCode)
                .eq("deleted", 0)
                .last("LIMIT 1");
        RoleDO role = roleMapper.selectOne(wrapper);
        return role != null ? role.getId() : null;
    }

    private String resolvePersonnelNodeType(TaskType taskType) {
        return taskType == TaskType.MANAGEMENT || taskType == TaskType.ACCEPTANCE ? "STAGE" : taskType.getValue();
    }

    private String resolveNodeRoleCode(TaskType taskType, String personnelNodeType) {
        if ("APPLICATION".equals(personnelNodeType)) {
            return "APPLICANT";
        }
        return switch (taskType) {
            case AUDIT -> "AUDITOR";
            case REVIEW -> "REVIEWER";
            case DECISION -> "DECISION_MAKER";
            case MANAGEMENT -> "MANAGER";
            case ACCEPTANCE -> "ACCEPTANCE_INSPECTOR";
            default -> null;
        };
    }

    private TaskType stageTypeToTaskType(String stageType) {
        if ("ACCEPTANCE".equals(stageType)) {
            return TaskType.ACCEPTANCE;
        }
        return TaskType.MANAGEMENT;
    }

    private int resolveRequiredCount(ReviewProjectDO project, TaskType taskType, Integer nodeSequence) {
        try {
            ProjectTypeSnapshot snapshot = objectMapper.convertValue(project.getSnapshotConfig(), ProjectTypeSnapshot.class);
            return resolveRequiredCount(snapshot, taskType, nodeSequence);
        } catch (Exception e) {
            log.warn("[TaskAssignment] failed to parse snapshot, fallback to default count: {}", e.getMessage());
            return DEFAULT_MANAGEMENT_COUNT;
        }
    }

    private int resolveRequiredCount(ProjectTypeSnapshot snapshot, TaskType taskType, Integer nodeSequence) {
        if (snapshot == null || snapshot.getApprovalRules() == null) {
            return DEFAULT_MANAGEMENT_COUNT;
        }
        if (taskType == TaskType.MANAGEMENT) {
            return DEFAULT_MANAGEMENT_COUNT;
        }
        ProjectTypeSnapshot.ApprovalRuleInfo rule = snapshot.getApprovalRules().get(buildNodeScope(taskType, nodeSequence));
        if (rule != null && rule.getRequiredReviewerCount() != null) {
            return rule.getRequiredReviewerCount();
        }
        return DEFAULT_MANAGEMENT_COUNT;
    }

    private String buildNodeScope(TaskType taskType, Integer nodeSequence) {
        if (taskType == TaskType.ACCEPTANCE) {
            return "ACCEPTANCE";
        }
        return taskType.getValue() + "_" + nodeSequence;
    }

    private void addLong(Set<Long> container, Object value) {
        if (value == null) {
            return;
        }
        try {
            container.add(Long.parseLong(String.valueOf(value)));
        } catch (NumberFormatException ignored) {
        }
    }

    private void addLong(List<Long> container, Object value) {
        if (value == null) {
            return;
        }
        try {
            container.add(Long.parseLong(String.valueOf(value)));
        } catch (NumberFormatException ignored) {
        }
    }
}
