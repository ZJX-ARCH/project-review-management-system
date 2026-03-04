-- liquibase formatted sql

-- changeset zjx:review-type-visibility-drop-1
-- preconditions onFail:MARK_RAN
-- precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME='review_type_visibility_config' AND TABLE_SCHEMA=DATABASE()
DROP TABLE `review_type_visibility_config`;
