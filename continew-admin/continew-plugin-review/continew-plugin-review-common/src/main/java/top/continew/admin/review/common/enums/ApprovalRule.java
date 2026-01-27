package top.continew.admin.review.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.continew.starter.core.enums.BaseEnum;

/**
 * 审批规则枚举
 *
 * @author zjx
 * @since 2026-01-27
 */
@Getter
@RequiredArgsConstructor
public enum ApprovalRule implements BaseEnum<String> {

    ALL_PASS("ALL_PASS", "全部通过", "所有审核员都同意才通过"),
    MAJORITY("MAJORITY", "多数通过", "超过半数审核员同意即通过"),
    ONE_PASS("ONE_PASS", "一票通过", "任意一个审核员同意即通过"),
    COMBINED("COMBINED", "组合规则", "平均分和最低分都要达标（评审专用）"),
    RATIO_PASS("RATIO_PASS", "比例通过", "通过票数达到阈值（如2/3）");

    private final String value;
    private final String description;
    private final String detail;
}
