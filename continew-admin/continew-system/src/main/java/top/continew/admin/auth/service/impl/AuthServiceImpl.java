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

package top.continew.admin.auth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.continew.admin.auth.LoginHandler;
import top.continew.admin.auth.LoginHandlerFactory;
import top.continew.admin.auth.enums.AuthTypeEnum;
import top.continew.admin.auth.model.req.LoginReq;
import top.continew.admin.auth.model.req.SetDefaultDeptReq;
import top.continew.admin.auth.model.req.SwitchDeptReq;
import top.continew.admin.auth.model.resp.LoginResp;
import top.continew.admin.auth.model.resp.RouteResp;
import top.continew.admin.auth.model.resp.UserDeptRolesResp;
import top.continew.admin.auth.service.AuthService;
import top.continew.admin.common.context.RoleContext;
import top.continew.admin.common.context.UserContext;
import top.continew.admin.common.context.UserContextHolder;
import top.continew.admin.common.enums.DisEnableStatusEnum;
import top.continew.admin.system.constant.SystemConstants;
import top.continew.admin.system.enums.MenuTypeEnum;
import top.continew.admin.system.model.entity.DeptDO;
import top.continew.admin.system.model.entity.RoleDO;
import top.continew.admin.system.model.entity.user.UserDO;
import top.continew.admin.system.model.resp.ClientResp;
import top.continew.admin.system.model.resp.MenuResp;
import top.continew.admin.system.service.ClientService;
import top.continew.admin.system.service.DeptService;
import top.continew.admin.system.service.MenuService;
import top.continew.admin.system.service.RoleService;
import top.continew.admin.system.service.UserRoleService;
import top.continew.admin.system.service.UserService;
import top.continew.starter.core.util.CollUtils;
import top.continew.starter.core.util.validation.CheckUtils;
import top.continew.starter.core.util.validation.ValidationUtils;
import top.continew.starter.extension.crud.annotation.TreeField;
import top.continew.starter.extension.crud.autoconfigure.CrudProperties;
import top.continew.starter.extension.crud.model.resp.LabelValueResp;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 认证业务实现
 *
 * @author Charles7c
 * @since 2022/12/21 21:49
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final LoginHandlerFactory loginHandlerFactory;
    private final ClientService clientService;
    private final RoleService roleService;
    private final MenuService menuService;
    private final UserRoleService userRoleService;
    private final DeptService deptService;
    private final UserService userService;
    private final CrudProperties crudProperties;

    @Override
    public LoginResp login(LoginReq req, HttpServletRequest request) {
        AuthTypeEnum authType = req.getAuthType();
        // 校验客户端
        ClientResp client = clientService.getByClientId(req.getClientId());
        ValidationUtils.throwIfNull(client, "客户端不存在");
        ValidationUtils.throwIf(DisEnableStatusEnum.DISABLE.equals(client.getStatus()), "客户端已禁用");
        ValidationUtils.throwIf(!client.getAuthType().contains(authType.getValue()), "该客户端暂未授权 [{}] 认证", authType
            .getDescription());
        // 获取处理器
        LoginHandler<LoginReq> loginHandler = loginHandlerFactory.getHandler(authType);
        // 登录前置处理
        loginHandler.preLogin(req, client, request);
        // 登录
        LoginResp loginResp = loginHandler.login(req, client, request);
        // 登录后置处理
        loginHandler.postLogin(req, client, request);
        return loginResp;
    }

    @Override
    public List<RouteResp> buildRouteTree(Long userId) {
        // 获取用户上下文中的当前部门ID
        UserContext userContext = UserContextHolder.getContext();
        Long deptId = userContext != null ? userContext.getDeptId() : null;
        
        // 使用当前部门ID查询角色，实现菜单按部门隔离
        Set<RoleContext> roleSet;
        if (deptId != null) {
            roleSet = roleService.listByUserIdAndDeptId(userId, deptId);
        } else {
            // 兼容没有部门的用户
            roleSet = roleService.listByUserId(userId);
        }
        
        if (CollUtil.isEmpty(roleSet)) {
            return new ArrayList<>(0);
        }
        // 查询菜单列表
        Set<MenuResp> menuSet = new LinkedHashSet<>();
        if (roleSet.stream().anyMatch(r -> SystemConstants.SUPER_ADMIN_ROLE_ID.equals(r.getId()))) {
            menuSet.addAll(menuService.listByRoleId(SystemConstants.SUPER_ADMIN_ROLE_ID));
        } else {
            roleSet.forEach(r -> menuSet.addAll(menuService.listByRoleId(r.getId())));
        }
        List<MenuResp> menuList = menuSet.stream().filter(m -> !MenuTypeEnum.BUTTON.equals(m.getType())).toList();
        if (CollUtil.isEmpty(menuList)) {
            return new ArrayList<>(0);
        }
        // 构建路由树
        TreeField treeField = MenuResp.class.getDeclaredAnnotation(TreeField.class);
        TreeNodeConfig treeNodeConfig = crudProperties.getTreeDictModel().genTreeNodeConfig(treeField);
        List<Tree<Long>> treeList = TreeUtil.build(menuList, treeField.rootId(), treeNodeConfig, (m, tree) -> {
            tree.setId(m.getId());
            tree.setParentId(m.getParentId());
            tree.setName(m.getTitle());
            tree.setWeight(m.getSort());
            tree.putExtra("type", m.getType().getValue());
            tree.putExtra("path", m.getPath());
            tree.putExtra("name", m.getName());
            tree.putExtra("component", m.getComponent());
            tree.putExtra("redirect", m.getRedirect());
            tree.putExtra("icon", m.getIcon());
            tree.putExtra("isExternal", m.getIsExternal());
            tree.putExtra("isCache", m.getIsCache());
            tree.putExtra("isHidden", m.getIsHidden());
            tree.putExtra("permission", m.getPermission());
            tree.putExtra("showInTabs", m.getShowInTabs());
        });
        return BeanUtil.copyToList(treeList, RouteResp.class);
    }

    @Override
    public void switchDept(SwitchDeptReq req) {
        Long userId = UserContextHolder.getUserId();
        Long deptId = req.getDeptId();
        
        // 检查用户是否在该部门有角色
        List<Long> roleIds = userRoleService.listRoleIdByUserIdAndDeptId(userId, deptId);
        CheckUtils.throwIf(CollUtil.isEmpty(roleIds), "您在该部门没有分配角色，无法切换");
        
        // 更新用户上下文
        UserContext userContext = UserContextHolder.getContext();
        userContext.setDeptId(deptId);
        userContext.setRoles(roleService.listByUserIdAndDeptId(userId, deptId));
        userContext.setPermissions(roleService.listPermissionByUserIdAndDeptId(userId, deptId));
        UserContextHolder.setContext(userContext);
    }

    @Override
    public List<LabelValueResp<Long>> listOptionalDepts() {
        Long userId = UserContextHolder.getUserId();

        // 获取用户拥有角色的部门ID列表
        List<Long> deptIds = userRoleService.listDeptIdByUserId(userId);
        if (CollUtil.isEmpty(deptIds)) {
            return new ArrayList<>(0);
        }

        // 查询部门信息
        List<DeptDO> deptList = deptService.listByIds(deptIds);
        return CollUtils.mapToList(deptList, dept -> new LabelValueResp<>(dept.getName(), dept.getId()));
    }

    @Override
    public UserDeptRolesResp getUserDeptRoles() {
        Long userId = UserContextHolder.getUserId();
        UserContext userContext = UserContextHolder.getContext();
        Long currentDeptId = userContext != null ? userContext.getDeptId() : null;

        // 获取用户的主部门ID（默认部门）
        Long mainDeptId = userService.getById(userId).getDeptId();

        // 获取用户所有有角色的部门ID列表
        List<Long> deptIds = userRoleService.listDeptIdByUserId(userId);
        if (CollUtil.isEmpty(deptIds)) {
            UserDeptRolesResp resp = new UserDeptRolesResp();
            resp.setCurrentDeptId(currentDeptId);
            resp.setMainDeptId(mainDeptId);
            resp.setDeptRoles(new ArrayList<>(0));
            return resp;
        }

        // 批量查询部门信息
        List<DeptDO> deptList = deptService.listByIds(deptIds);
        Map<Long, String> deptIdNameMap = deptList.stream()
            .collect(Collectors.toMap(DeptDO::getId, DeptDO::getName));

        // 查询每个部门下的角色信息
        List<UserDeptRolesResp.DeptRoleInfo> deptRoles = new ArrayList<>();
        for (Long deptId : deptIds) {
            UserDeptRolesResp.DeptRoleInfo deptRoleInfo = new UserDeptRolesResp.DeptRoleInfo();
            deptRoleInfo.setDeptId(deptId);
            deptRoleInfo.setDeptName(deptIdNameMap.get(deptId));

            // 查询该部门下用户的角色
            Set<RoleContext> roles = roleService.listByUserIdAndDeptId(userId, deptId);
            List<Long> roleIds = roles.stream()
                .map(RoleContext::getId)
                .collect(Collectors.toList());

            // 根据角色ID查询角色名称
            List<String> roleNames = new ArrayList<>();
            if (CollUtil.isNotEmpty(roleIds)) {
                List<RoleDO> roleDOList = roleService.listByIds(roleIds);
                roleNames = roleDOList.stream()
                    .map(RoleDO::getName)
                    .collect(Collectors.toList());
            }
            deptRoleInfo.setRoleNames(roleNames);

            deptRoles.add(deptRoleInfo);
        }

        UserDeptRolesResp resp = new UserDeptRolesResp();
        resp.setCurrentDeptId(currentDeptId);
        resp.setMainDeptId(mainDeptId);
        resp.setDeptRoles(deptRoles);
        return resp;
    }

    @Override
    public void setDefaultDept(SetDefaultDeptReq req) {
        Long userId = UserContextHolder.getUserId();
        Long deptId = req.getDeptId();

        // 验证用户在该部门是否有角色
        List<Long> roleIds = userRoleService.listRoleIdByUserIdAndDeptId(userId, deptId);
        CheckUtils.throwIf(CollUtil.isEmpty(roleIds), "您在该部门没有分配角色，无法设为默认部门");

        // 更新用户的主部门
        userService.lambdaUpdate()
            .set(UserDO::getDeptId, deptId)
            .eq(UserDO::getId, userId)
            .update();
    }
}
