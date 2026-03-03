-- liquibase formatted sql

-- changeset zjx:review-type-personnel-node-key-1
ALTER TABLE `review_type_personnel_config`
    ADD COLUMN `node_type`     varchar(30) NULL COMMENT '节点类型（APPLICATION/AUDIT/REVIEW/DECISION/STAGE）' AFTER `type_id`,
    ADD COLUMN `node_sequence` int(11)     NULL COMMENT '节点序号（轮次/阶段顺序，APPLICATION为NULL）' AFTER `node_type`;

-- changeset zjx:review-type-personnel-node-key-2
-- preconditions onFail:MARK_RAN
-- precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_NAME='review_type_personnel_config' AND INDEX_NAME='uk_type_role' AND SEQ_IN_INDEX=1 AND TABLE_SCHEMA=DATABASE()
ALTER TABLE `review_type_personnel_config` DROP INDEX `uk_type_role`;

-- changeset zjx:review-type-personnel-node-key-2b
-- preconditions onFail:MARK_RAN
-- precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_NAME='review_type_personnel_config' AND INDEX_NAME='idx_role_type' AND SEQ_IN_INDEX=1 AND TABLE_SCHEMA=DATABASE()
ALTER TABLE `review_type_personnel_config` DROP INDEX `idx_role_type`;

-- changeset zjx:review-type-personnel-node-key-2c
-- preconditions onFail:MARK_RAN
-- precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='review_type_personnel_config' AND COLUMN_NAME='role_type' AND TABLE_SCHEMA=DATABASE()
ALTER TABLE `review_type_personnel_config` DROP COLUMN `role_type`;

-- changeset zjx:review-type-personnel-node-key-3
-- preconditions onFail:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_NAME='review_type_personnel_config' AND INDEX_NAME='idx_node_type' AND SEQ_IN_INDEX=1 AND TABLE_SCHEMA=DATABASE()
ALTER TABLE `review_type_personnel_config`
    ADD INDEX `idx_node_type`(`node_type`, `node_sequence`, `deleted`);
