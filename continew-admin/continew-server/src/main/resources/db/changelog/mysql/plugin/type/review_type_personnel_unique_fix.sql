-- liquibase formatted sql

-- changeset zjx:review-type-personnel-unique-fix-1
-- preconditions onFail:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_NAME='review_type_personnel_config' AND INDEX_NAME='uk_type_node_scope' AND TABLE_SCHEMA=DATABASE()
-- comment 添加唯一约束：同一类型的同一节点不允许配置相同的 scopeType（防止 selectOne 报 TooManyResults）
ALTER TABLE `review_type_personnel_config`
    ADD UNIQUE INDEX `uk_type_node_scope` (`type_id`, `node_type`, `node_sequence`, `scope_type`, `deleted`);
