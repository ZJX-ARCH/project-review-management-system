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

package top.continew.admin.system.model.req.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 角色分配请求参数
 *
 * @author zjx
 * @since 2026/1/26 16:00
 */
@Data
@Schema(description = "角色分配请求参数")
public class RoleAssignReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户部门分配列表
     */
    @Schema(description = "用户部门分配列表")
    @NotEmpty(message = "用户列表不能为空")
    private List<UserDeptItem> userDepts;

    /**
     * 用户部门项
     */
    @Data
    @Schema(description = "用户部门项")
    public static class UserDeptItem implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 用户 ID
         */
        @Schema(description = "用户 ID", example = "1")
        @NotNull(message = "用户ID不能为空")
        private Long userId;

        /**
         * 部门 ID（NULL表示全局角色）
         */
        @Schema(description = "部门 ID", example = "1")
        private Long deptId;
    }
}
