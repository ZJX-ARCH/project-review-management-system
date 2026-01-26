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

package top.continew.admin.auth.model.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 用户部门角色信息响应参数
 *
 * @author Charles7c
 * @since 2026/01/26 15:00
 */
@Data
@Schema(description = "用户部门角色信息响应参数")
public class UserDeptRolesResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 当前部门 ID
     */
    @Schema(description = "当前部门 ID", example = "1")
    private Long currentDeptId;

    /**
     * 主部门 ID（默认部门）
     */
    @Schema(description = "主部门 ID", example = "1")
    private Long mainDeptId;

    /**
     * 部门角色列表
     */
    @Schema(description = "部门角色列表")
    private List<DeptRoleInfo> deptRoles;

    /**
     * 部门角色信息
     */
    @Data
    @Schema(description = "部门角色信息")
    public static class DeptRoleInfo implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 部门 ID
         */
        @Schema(description = "部门 ID", example = "1")
        private Long deptId;

        /**
         * 部门名称
         */
        @Schema(description = "部门名称", example = "技术部")
        private String deptName;

        /**
         * 角色名称列表
         */
        @Schema(description = "角色名称列表", example = "[\"系统管理员\", \"开发人员\"]")
        private List<String> roleNames;
    }
}
