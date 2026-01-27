package top.continew.admin.review.common.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 数据范围SQL工具类
 *
 * 根据部门的数据范围配置生成SQL条件
 *
 * @author zjx
 * @since 2026-01-27
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DataScopeUtil {

    /**
     * 生成部门数据范围SQL
     *
     * @param deptId 当前用户部门ID
     * @param deptAlias 部门表别名
     * @param scopeType 数据范围类型（DEPT_AND_SUB/DEPT）
     * @return SQL条件
     */
    public static String generateDeptScopeSql(Long deptId, String deptAlias, String scopeType) {
        if (deptId == null) {
            return "1=0"; // 无部门，无权限
        }

        String deptColumn = StrUtil.isBlank(deptAlias)
            ? "dept_id"
            : deptAlias + ".dept_id";

        if ("DEPT_AND_SUB".equals(scopeType)) {
            // 当前部门及以下
            return String.format(
                "(%s = %d OR %s IN (SELECT id FROM sys_dept WHERE parent_id = %d))",
                deptColumn, deptId, deptColumn, deptId
            );
        } else {
            // 仅当前部门
            return String.format("%s = %d", deptColumn, deptId);
        }
    }

    /**
     * 生成用户数据范围SQL
     *
     * @param userIds 指定用户ID列表
     * @param userAlias 用户表别名
     * @return SQL条件
     */
    public static String generateUserScopeSql(List<Long> userIds, String userAlias) {
        if (CollUtil.isEmpty(userIds)) {
            return "1=0"; // 无指定用户，无权限
        }

        String userColumn = StrUtil.isBlank(userAlias)
            ? "creator_id"
            : userAlias + ".creator_id";

        return String.format("%s IN (%s)", userColumn, CollUtil.join(userIds, ","));
    }
}
