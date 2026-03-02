package top.continew.admin.review.type.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.continew.starter.core.enums.BaseEnum;

/**
 * 审批通过模式枚举
 *
 * @author zjx
 * @since 2026-03-02
 */
@Getter
@RequiredArgsConstructor
public enum ApprovalModeEnum implements BaseEnum<String> {

    VOTE_ALL_PASS("VOTE_ALL_PASS", "全部通过"),
    VOTE_MAJORITY_PASS("VOTE_MAJORITY_PASS", "多数通过"),
    VOTE_ONE_PASS("VOTE_ONE_PASS", "一票通过"),
    SCORE_PASS("SCORE_PASS", "评分通过");

    private final String value;
    private final String description;
}
