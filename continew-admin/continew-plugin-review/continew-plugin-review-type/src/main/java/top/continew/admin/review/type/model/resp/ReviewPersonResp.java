package top.continew.admin.review.type.model.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 评审人员搜索响应 DTO
 * 供节点人员范围配置：按角色过滤/按 ID 回显用户
 *
 * @author zjx
 * @since 2026-03-05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewPersonResp {

    /**
     * 用户 ID（String 防止前端 JS 精度丢失）
     */
    private String id;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 用户名
     */
    private String username;
}
