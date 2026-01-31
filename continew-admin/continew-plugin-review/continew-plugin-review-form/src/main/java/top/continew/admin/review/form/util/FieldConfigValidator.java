package top.continew.admin.review.form.util;

import cn.hutool.core.collection.CollUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import top.continew.admin.review.form.model.req.FormFieldReq;
import top.continew.starter.core.exception.BusinessException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 字段配置验证工具类
 *
 * @author zjx
 * @since 2026-01-31
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FieldConfigValidator {

    /**
     * 验证字段编码在同一模板内的唯一性
     *
     * @param fields 字段列表
     * @throws BusinessException 如果有重复的字段编码
     */
    public static void validateFieldCodeUnique(List<FormFieldReq> fields) {
        if (CollUtil.isEmpty(fields)) {
            return;
        }

        // 提取所有字段编码
        List<String> fieldCodes = fields.stream()
            .map(FormFieldReq::getFieldCode)
            .collect(Collectors.toList());

        // 使用Set检查是否有重复
        Set<String> fieldCodeSet = new HashSet<>(fieldCodes);
        if (fieldCodeSet.size() != fieldCodes.size()) {
            throw new BusinessException("字段编码不能重复");
        }
    }

    /**
     * 验证字段排序的连续性
     *
     * @param fields 字段列表
     * @throws BusinessException 如果排序不连续或有重复
     */
    public static void validateFieldOrder(List<FormFieldReq> fields) {
        if (CollUtil.isEmpty(fields)) {
            return;
        }

        // 提取所有排序值并排序
        List<Integer> orders = fields.stream()
            .map(FormFieldReq::getSort)
            .sorted()
            .collect(Collectors.toList());

        // 使用Set检查是否有重复序号
        Set<Integer> orderSet = new HashSet<>(orders);
        if (orderSet.size() != orders.size()) {
            throw new BusinessException("字段排序不能有重复");
        }

        // 验证序号从0或1开始且连续
        int expectedStart = orders.get(0);
        if (expectedStart != 0 && expectedStart != 1) {
            throw new BusinessException("字段排序必须从0或1开始");
        }

        for (int i = 0; i < orders.size(); i++) {
            int expectedOrder = expectedStart + i;
            if (!orders.get(i).equals(expectedOrder)) {
                throw new BusinessException("字段排序必须连续，不能跳号");
            }
        }
    }

    /**
     * 验证字段span布局合理性(可选验证)
     *
     * @param fields 字段列表
     */
    public static void validateFieldSpan(List<FormFieldReq> fields) {
        if (CollUtil.isEmpty(fields)) {
            return;
        }

        for (FormFieldReq field : fields) {
            Integer span = field.getSpan();

            // span必须在1-24之间
            if (span < 1 || span > 24) {
                throw new BusinessException(
                    String.format("字段[%s]的span值必须在1-24之间", field.getFieldName()));
            }
        }
    }
}
