package top.continew.admin.review.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.continew.starter.core.enums.BaseEnum;

/**
 * 修改策略枚举
 *
 * @author zjx
 * @since 2026-01-27
 */
@Getter
@RequiredArgsConstructor
public enum ModificationStrategy implements BaseEnum<String> {

    RESTART_FROM_FIRST("RESTART_FROM_FIRST", "回到第1轮", "修改后从当前节点第1轮重新开始"),
    RESTART_FROM_CURRENT("RESTART_FROM_CURRENT", "回到当前轮", "修改后从当前轮重新开始"),
    RESTART_FROM_SPECIFIED("RESTART_FROM_SPECIFIED", "回到指定轮", "修改后从指定轮开始");

    private final String value;
    private final String description;
    private final String detail;
}
