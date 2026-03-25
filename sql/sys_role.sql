/*
 Navicat Premium Dump SQL

 Source Server         : aaa
 Source Server Type    : MySQL
 Source Server Version : 100508 (10.5.8-MariaDB)
 Source Host           : localhost:3306
 Source Schema         : continew_admin

 Target Server Type    : MySQL
 Target Server Version : 100508 (10.5.8-MariaDB)
 File Encoding         : 65001

 Date: 25/03/2026 09:16:52
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
  `code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '编码',
  `data_scope` tinyint(1) NOT NULL DEFAULT 4 COMMENT '数据权限（1：全部数据权限；2：本部门及以下数据权限；3：本部门数据权限；4：仅本人数据权限；5：自定义数据权限）',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `sort` int NOT NULL DEFAULT 999 COMMENT '排序',
  `is_system` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否为系统内置数据',
  `menu_check_strictly` bit(1) NULL DEFAULT b'1' COMMENT '菜单选择是否父子节点关联',
  `dept_check_strictly` bit(1) NULL DEFAULT b'1' COMMENT '部门选择是否父子节点关联',
  `create_user` bigint NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `deleted` bigint NOT NULL DEFAULT 0 COMMENT '是否已删除（0：否；id：是）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name` ASC, `deleted` ASC) USING BTREE,
  UNIQUE INDEX `uk_code`(`code` ASC, `deleted` ASC) USING BTREE,
  INDEX `idx_create_user`(`create_user` ASC) USING BTREE,
  INDEX `idx_update_user`(`update_user` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 806225491869622423 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'super_admin', 1, '系统初始角色', 0, b'1', b'1', b'1', 1, '2026-01-23 18:05:58', NULL, NULL, 0);
INSERT INTO `sys_role` VALUES (2, '系统管理员', 'sys_admin', 1, NULL, 1, b'0', b'1', b'1', 1, '2026-01-23 18:05:58', 1, '2026-01-26 18:08:16', 2);
INSERT INTO `sys_role` VALUES (3, '普通用户', 'general', 4, NULL, 2, b'0', b'1', b'1', 1, '2026-01-23 18:05:58', 1, '2026-01-26 18:07:43', 3);
INSERT INTO `sys_role` VALUES (547888897925840927, '测试人员', 'tester', 5, NULL, 3, b'0', b'1', b'1', 1, '2026-01-23 18:05:58', 1, '2026-01-26 18:08:45', 547888897925840927);
INSERT INTO `sys_role` VALUES (547888897925840928, '研发人员', 'developer', 4, NULL, 4, b'0', b'1', b'1', 1, '2026-01-23 18:05:58', 1, '2026-01-26 18:08:21', 547888897925840928);
INSERT INTO `sys_role` VALUES (806224734843887736, '申请人员', 'APPLICANT', 4, '创建和提交项目申请', 999, b'0', b'1', b'1', 1, '2026-01-26 18:09:15', NULL, NULL, 0);
INSERT INTO `sys_role` VALUES (806224824899788923, '审核人员', 'AUDITOR', 4, '处理分配的审核任务', 999, b'0', b'1', b'1', 1, '2026-01-26 18:09:37', NULL, NULL, 0);
INSERT INTO `sys_role` VALUES (806224894021918846, '评审人员', 'REVIEWER', 4, '处理分配的评审任务', 999, b'0', b'1', b'1', 1, '2026-01-26 18:09:53', NULL, NULL, 0);
INSERT INTO `sys_role` VALUES (806224959423701121, '决策人员', 'DECISION_MAKER', 4, '处理分配的决策任务', 999, b'0', b'1', b'1', 1, '2026-01-26 18:10:09', NULL, NULL, 0);
INSERT INTO `sys_role` VALUES (806225017640640644, '管理人员', 'MANAGER', 4, '管理立项后项目的执行', 999, b'0', b'1', b'1', 1, '2026-01-26 18:10:23', NULL, NULL, 0);
INSERT INTO `sys_role` VALUES (806225069176053895, '验收人员', 'ACCEPTANCE_INSPECTOR', 4, '处理分配的验收任务', 999, b'0', b'1', b'1', 1, '2026-01-26 18:10:35', NULL, NULL, 0);
INSERT INTO `sys_role` VALUES (806225197471424650, '项目管理员', 'PROJECT_ADMIN', 2, '管理项目、调整人员分配、审核阶段成果', 999, b'0', b'1', b'1', 1, '2026-01-26 18:11:06', NULL, NULL, 0);
INSERT INTO `sys_role` VALUES (806225272348139661, '类型管理员', 'TYPE_ADMIN', 2, '配置项目类型、选择模板、配置人员范围	', 999, b'0', b'1', b'1', 1, '2026-01-26 18:11:23', NULL, NULL, 0);
INSERT INTO `sys_role` VALUES (806225358390091920, '流程管理员', 'FLOW_ADMIN', 2, '管理流程模板、配置轮次和审批规则', 999, b'0', b'1', b'1', 1, '2026-01-26 18:11:44', NULL, NULL, 0);
INSERT INTO `sys_role` VALUES (806225422177067155, '模板管理员', 'TEMPLATE_ADMIN', 2, '管理表单模板和文件库', 999, b'0', b'1', b'1', 1, '2026-01-26 18:11:59', NULL, NULL, 0);
INSERT INTO `sys_role` VALUES (806225491869622422, '人员管理员', 'USER_ADMIN', 2, '管理用户和角色分配	', 999, b'0', b'1', b'1', 1, '2026-01-26 18:12:16', NULL, NULL, 0);

SET FOREIGN_KEY_CHECKS = 1;
