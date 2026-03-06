-- liquibase formatted sql

-- changeset zjx:review-type-approval-drop-reject-back-to-1
ALTER TABLE `review_type_approval_config` DROP COLUMN `reject_back_to`;
