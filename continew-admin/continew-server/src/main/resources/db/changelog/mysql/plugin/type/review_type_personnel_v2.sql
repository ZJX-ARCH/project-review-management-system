-- liquibase formatted sql

-- changeset zjx:review-type-personnel-node-key-1
ALTER TABLE `review_type_personnel_config`
    ADD COLUMN `node_type`     varchar(30) NULL COMMENT '节点类型（APPLICATION/AUDIT/REVIEW/DECISION/STAGE）' AFTER `type_id`,
    ADD COLUMN `node_sequence` int(11)     NULL COMMENT '节点序号（轮次/阶段顺序，APPLICATION为NULL）' AFTER `node_type`;

-- changeset zjx:review-type-personnel-node-key-2
ALTER TABLE `review_type_personnel_config` DROP COLUMN `role_type`;
