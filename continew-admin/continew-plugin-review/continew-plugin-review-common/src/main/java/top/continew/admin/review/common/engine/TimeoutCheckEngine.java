package top.continew.admin.review.common.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 超时检查引擎
 *
 * 核心功能：
 * 1. 计算是否超时
 * 2. 计算超时天数
 * 3. 提供超时判定逻辑
 *
 * @author zjx
 * @since 2026-01-27
 */
@Slf4j
@Component
public class TimeoutCheckEngine {

    /**
     * 判断是否超时
     *
     * @param plannedEndTime 计划结束时间
     * @return true=已超时, false=未超时
     */
    public boolean isTimeout(LocalDateTime plannedEndTime) {
        if (plannedEndTime == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(plannedEndTime);
    }

    /**
     * 计算超时天数
     *
     * @param plannedEndTime 计划结束时间
     * @return 超时天数（未超时返回0）
     */
    public long calculateTimeoutDays(LocalDateTime plannedEndTime) {
        if (plannedEndTime == null) {
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(plannedEndTime)) {
            return 0; // 未超时
        }

        return ChronoUnit.DAYS.between(plannedEndTime, now);
    }

    /**
     * 计算剩余天数
     *
     * @param plannedEndTime 计划结束时间
     * @return 剩余天数（已超时返回负数）
     */
    public long calculateRemainingDays(LocalDateTime plannedEndTime) {
        if (plannedEndTime == null) {
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();
        return ChronoUnit.DAYS.between(now, plannedEndTime);
    }

    /**
     * 获取超时描述
     *
     * @param plannedEndTime 计划结束时间
     * @return 超时描述（如："已超时5天"、"剩余3天"）
     */
    public String getTimeoutDescription(LocalDateTime plannedEndTime) {
        if (plannedEndTime == null) {
            return "未设置截止时间";
        }

        long remaining = calculateRemainingDays(plannedEndTime);

        if (remaining > 0) {
            return String.format("剩余%d天", remaining);
        } else if (remaining < 0) {
            return String.format("已超时%d天", Math.abs(remaining));
        } else {
            return "今天截止";
        }
    }
}
