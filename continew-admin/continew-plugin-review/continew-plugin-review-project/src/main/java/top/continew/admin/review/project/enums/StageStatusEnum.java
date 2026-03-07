package top.continew.admin.review.project.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.continew.starter.core.enums.BaseEnum;

/**
 * 管理阶段状态枚举
 *
 * @author zjx
 * @since 2026-03-07
 */
@Getter
@RequiredArgsConstructor
public enum StageStatusEnum implements BaseEnum<String> {

    /** 待激活（前序阶段未完成） */
    PENDING("PENDING", "待激活"),

    /** 执行中（当前阶段已激活，申请人可提交成果） */
    IN_PROGRESS("IN_PROGRESS", "执行中"),

    /** 已提交（申请人已提交成果，等待管理员审核） */
    SUBMITTED("SUBMITTED", "已提交"),

    /** 已完成（管理员审核通过） */
    COMPLETED("COMPLETED", "已完成"),

    /** 已驳回（管理员驳回，申请人需重做） */
    REJECTED("REJECTED", "已驳回");

    private final String value;
    private final String description;
}
