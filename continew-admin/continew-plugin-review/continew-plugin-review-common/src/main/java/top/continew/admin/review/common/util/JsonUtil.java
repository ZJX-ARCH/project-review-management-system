package top.continew.admin.review.common.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * JSON工具类
 *
 * 封装动态表单JSON字段的处理逻辑
 *
 * @author zjx
 * @since 2026-01-27
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonUtil {

    /**
     * 解析JSON字符串为Map
     */
    public static Map<String, Object> parseJson(String json) {
        if (StrUtil.isBlank(json)) {
            return Map.of();
        }
        return JSONUtil.toBean(json, Map.class);
    }

    /**
     * 将Map转为JSON字符串
     */
    public static String toJsonString(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return "{}";
        }
        return JSONUtil.toJsonStr(map);
    }

    /**
     * 从JSON中获取字段值
     */
    public static Object getFieldValue(String json, String fieldName) {
        Map<String, Object> map = parseJson(json);
        return map.get(fieldName);
    }

    /**
     * 向JSON中设置字段值
     */
    public static String setFieldValue(String json, String fieldName, Object value) {
        Map<String, Object> map = parseJson(json);
        map.put(fieldName, value);
        return toJsonString(map);
    }
}
