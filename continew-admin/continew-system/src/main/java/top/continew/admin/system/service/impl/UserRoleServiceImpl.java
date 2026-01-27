/*
 * Copyright (c) 2022-present Charles7c Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.continew.admin.system.service.impl;

import cn.crane4j.annotation.AutoOperate;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.common.context.RoleContext;
import top.continew.admin.common.enums.RoleCodeEnum;
import top.continew.admin.system.constant.SystemConstants;
import top.continew.admin.system.mapper.UserRoleMapper;
import top.continew.admin.system.model.entity.RoleDO;
import top.continew.admin.system.model.entity.UserRoleDO;
import top.continew.admin.system.model.entity.user.UserDO;
import top.continew.admin.system.model.query.RoleUserQuery;
import top.continew.admin.system.model.req.role.RoleAssignReq;
import top.continew.admin.system.model.resp.role.RoleUserResp;
import top.continew.admin.system.service.RoleService;
import top.continew.admin.system.service.UserRoleService;
import top.continew.admin.system.service.UserService;
import top.continew.starter.core.util.CollUtils;
import top.continew.starter.core.util.validation.CheckUtils;
import top.continew.starter.data.util.QueryWrapperHelper;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户和角色业务实现
 *
 * @author Charles7c
 * @since 2023/2/20 21:30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleMapper baseMapper;
    @Lazy
    @Resource
    private RoleService roleService;
    @Lazy
    @Resource
    private UserService userService;

    @Override
    // 移除 @AutoOperate 注解，因为我们手动填充角色信息
    // @AutoOperate(type = RoleUserResp.class, on = "list")
    public PageResp<RoleUserResp> pageUser(RoleUserQuery query, PageQuery pageQuery) {
        String description = query.getDescription();
        QueryWrapper<UserRoleDO> queryWrapper = new QueryWrapper<UserRoleDO>().eq("t1.role_id", query.getRoleId())
            .and(StrUtil.isNotBlank(description), q -> q.like("t2.username", description)
                .or()
                .like("t2.nickname", description)
                .or()
                .like("t2.description", description));
        QueryWrapperHelper.sort(queryWrapper, pageQuery.getSort());

        IPage<RoleUserResp> page = baseMapper.selectUserPage(new Page<>(pageQuery.getPage(), pageQuery
            .getSize()), queryWrapper);

        // 填充每个用户在其部门的所有角色
        if (CollUtil.isNotEmpty(page.getRecords())) {
            fillDeptRoles(page.getRecords());
        }

        return PageResp.build(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRolesToUser(List<Long> roleIds, Long userId) {
        // 获取用户的主部门
        UserDO user = userService.getById(userId);
        Long deptId = user.getDeptId();
        // 调用新方法
        return this.assignRolesToUserByDept(roleIds, userId, deptId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRolesToUserByDept(List<Long> roleIds, Long userId, Long deptId) {
        // 系统内置用户保护
        UserDO userDO = userService.getById(userId);
        if (Boolean.TRUE.equals(userDO.getIsSystem())) {
            Collection<Long> disjunctionRoleIds = CollUtil.disjunction(
                roleIds,
                this.listRoleIdByUserIdAndDeptId(userId, deptId)
            );
            CheckUtils.throwIfNotEmpty(disjunctionRoleIds,
                "[{}] 是系统内置用户，不允许变更角色", userDO.getNickname());
        }

        // 超级管理员和租户管理员角色不允许分配
        CheckUtils.throwIf(roleIds.contains(SystemConstants.SUPER_ADMIN_ROLE_ID),
            "不允许分配超级管理员角色");
        Set<String> roleCodeSet = CollUtils.mapToSet(
            roleService.listByUserId(userId),
            RoleContext::getCode
        );
        CheckUtils.throwIf(roleCodeSet.contains(RoleCodeEnum.TENANT_ADMIN.getCode()),
            "不允许分配系统管理员角色");

        // 检查是否有变更
        List<Long> oldRoleIdList = this.listRoleIdByUserIdAndDeptId(userId, deptId);
        if (CollUtil.isEmpty(CollUtil.disjunction(roleIds, oldRoleIdList))) {
            return false;
        }

        // 删除该用户在该部门的原有角色
        baseMapper.lambdaUpdate()
            .eq(UserRoleDO::getUserId, userId)
            .eq(UserRoleDO::getDeptId, deptId)
            .remove();

        // 保存新角色
        List<UserRoleDO> userRoleList = CollUtils.mapToList(
            roleIds,
            roleId -> new UserRoleDO(userId, deptId, roleId)
        );
        return baseMapper.insertBatch(userRoleList);
    }

    @Override
    public boolean assignRoleToUsers(Long roleId, List<Long> userIds) {
        List<UserRoleDO> userRoleList = CollUtils.mapToList(userIds, userId -> new UserRoleDO(userId, roleId));
        return baseMapper.insertBatch(userRoleList);
    }

    @Override
    public boolean assignRoleToUsersWithDept(Long roleId, List<RoleAssignReq.UserDeptItem> userDepts) {
        List<UserRoleDO> userRoleList = CollUtils.mapToList(userDepts,
            item -> new UserRoleDO(item.getUserId(), item.getDeptId(), roleId));
        return baseMapper.insertBatch(userRoleList);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        baseMapper.deleteByIds(ids);
    }

    @Override
    public void deleteByUserIds(List<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return;
        }
        baseMapper.lambdaUpdate().in(UserRoleDO::getUserId, userIds).remove();
    }

    @Override
    public void saveBatch(List<UserRoleDO> list) {
        baseMapper.insert(list);
    }

    @Override
    public List<Long> listRoleIdByUserId(Long userId) {
        return baseMapper.lambdaQuery()
            .select(UserRoleDO::getRoleId)
            .eq(UserRoleDO::getUserId, userId)
            .list()
            .stream()
            .map(UserRoleDO::getRoleId)
            .toList();
    }

    @Override
    public List<UserRoleDO> listByUserId(Long userId) {
        return baseMapper.lambdaQuery()
            .eq(UserRoleDO::getUserId, userId)
            .list();
    }

    @Override
    public List<Long> listUserIdByRoleId(Long roleId) {
        return baseMapper.lambdaQuery()
            .select(UserRoleDO::getUserId)
            .eq(UserRoleDO::getRoleId, roleId)
            .list()
            .stream()
            .map(UserRoleDO::getUserId)
            .toList();
    }

    @Override
    public boolean isRoleIdExists(List<Long> roleIds) {
        if (CollUtil.isEmpty(roleIds)) {
            return false;
        }
        return baseMapper.lambdaQuery().in(UserRoleDO::getRoleId, roleIds).exists();
    }

    @Override
    public List<Long> listDeptIdByUserIdAndRoleId(Long userId, Long roleId) {
        return baseMapper.lambdaQuery()
            .select(UserRoleDO::getDeptId)
            .eq(UserRoleDO::getUserId, userId)
            .eq(UserRoleDO::getRoleId, roleId)
            .isNotNull(UserRoleDO::getDeptId)
            .list()
            .stream()
            .map(UserRoleDO::getDeptId)
            .distinct()
            .toList();
    }

    @Override
    public List<Long> listRoleIdByUserIdAndDeptId(Long userId, Long deptId) {
        return baseMapper.lambdaQuery()
            .select(UserRoleDO::getRoleId)
            .eq(UserRoleDO::getUserId, userId)
            .and(wrapper -> wrapper
                .eq(UserRoleDO::getDeptId, deptId)
                .or()
                .isNull(UserRoleDO::getDeptId) // 包含全局角色
            )
            .list()
            .stream()
            .map(UserRoleDO::getRoleId)
            .toList();
    }

    @Override
    public List<Long> listDeptIdByUserId(Long userId) {
        return baseMapper.lambdaQuery()
            .select(UserRoleDO::getDeptId)
            .eq(UserRoleDO::getUserId, userId)
            .isNotNull(UserRoleDO::getDeptId)
            .list()
            .stream()
            .map(UserRoleDO::getDeptId)
            .distinct()
            .toList();
    }

    /**
     * 填充角色用户列表中每条记录的角色信息
     * 显示该用户在该部门的所有角色
     *
     * @param roleUserList 角色用户列表
     */
    private void fillDeptRoles(List<RoleUserResp> roleUserList) {
        if (CollUtil.isEmpty(roleUserList)) {
            return;
        }

        // 收集所有用户ID
        Set<Long> userIds = roleUserList.stream()
            .map(RoleUserResp::getUserId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        // 查询这些用户的所有角色关联
        List<UserRoleDO> allUserRoles = baseMapper.lambdaQuery()
            .in(UserRoleDO::getUserId, userIds)
            .list();

        // 收集所有角色ID
        Set<Long> roleIds = allUserRoles.stream()
            .map(UserRoleDO::getRoleId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        // 查询角色信息，构建角色ID到角色名称的映射
        Map<Long, String> roleNameMap = new HashMap<>();
        if (CollUtil.isNotEmpty(roleIds)) {
            List<RoleDO> roles = roleService.listByIds(roleIds);
            roleNameMap = roles.stream()
                .collect(Collectors.toMap(RoleDO::getId, RoleDO::getName));
        }

        // 构建用户+部门 -> 角色列表的映射
        Map<String, List<UserRoleDO>> userDeptRolesMap = allUserRoles.stream()
            .collect(Collectors.groupingBy(ur -> ur.getUserId() + "_" + ur.getDeptId()));

        // 为每个记录填充角色信息
        for (RoleUserResp roleUser : roleUserList) {
            String key = roleUser.getUserId() + "_" + roleUser.getDeptId();
            List<UserRoleDO> deptRoles = userDeptRolesMap.get(key);

            if (CollUtil.isNotEmpty(deptRoles)) {
                // 设置该用户在该部门的所有角色ID
                List<Long> deptRoleIds = deptRoles.stream()
                    .map(UserRoleDO::getRoleId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
                roleUser.setRoleIds(deptRoleIds);

                // 设置该用户在该部门的所有角色名称
                List<String> deptRoleNames = deptRoleIds.stream()
                    .map(roleNameMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
                roleUser.setRoleNames(deptRoleNames);
            }
        }
    }
}
