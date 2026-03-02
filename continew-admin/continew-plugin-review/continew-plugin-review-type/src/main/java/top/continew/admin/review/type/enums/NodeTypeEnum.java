package top.continew.admin.review.type.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.continew.starter.core.enums.BaseEnum;

/**
 * 节点类型枚举
 *
 * @author zjx
 * @since 2026-03-02
 */
@Getter
@RequiredArgsConstructor
public enum NodeTypeEnum implements BaseEnum<String> {

    APPLICATION("APPLICATION", "申请节点"),
    AUDIT("AUDIT", "审核节点"),
    REVIEW("REVIEW", "评审节点"),
    DECISION("DECISION", "决策节点"),
    STAGE("STAGE", "管理阶段");

    private final String value;
    private final String description;
}
