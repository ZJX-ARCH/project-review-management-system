package top.continew.admin.review.project.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.continew.starter.core.enums.BaseEnum;

/**
 * 任务状态枚举
 *
 * @author zjx
 * @since 2026-03-07
 */
@Getter
@RequiredArgsConstructor
public enum TaskStatusEnum implements BaseEnum<String> {

    /** 待处理（已分配，尚未操作） */
    PENDING("PENDING", "待处理"),

    /** 已暂存（填写中，尚未提交） */
    SAVED("SAVED", "已暂存"),

    /** 已完成（已提交决策） */
    COMPLETED("COMPLETED", "已完成"),

    /** 已转办（转给他人，不可再操作） */
    TRANSFERRED("TRANSFERRED", "已转办"),

    /** 已取消（项目终止或阶段回退导致作废） */
    CANCELLED("CANCELLED", "已取消");

    private final String value;
    private final String description;

    /**
     * 判断是否为活跃状态（可操作）
     */
    public boolean isActive() {
        return this == PENDING || this == SAVED;
    }
}
