package top.continew.admin.review.type.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.continew.starter.core.enums.BaseEnum;

/**
 * 评分汇总算法枚举
 *
 * @author zjx
 * @since 2026-03-02
 */
@Getter
@RequiredArgsConstructor
public enum ScoreCalcMethodEnum implements BaseEnum<String> {

    WEIGHTED_AVG("WEIGHTED_AVG", "加权平均"),
    SIMPLE_AVG("SIMPLE_AVG", "简单平均"),
    MAX_SCORE("MAX_SCORE", "取最高分"),
    MIN_SCORE("MIN_SCORE", "取最低分");

    private final String value;
    private final String description;
}
