/*
 Navicat Premium Data Transfer

 Source Server         : MySQL-Docker
 Source Server Type    : MySQL
 Source Server Version : 80043
 Source Host           : localhost:3307
 Source Schema         : db_iam

 Target Server Type    : MySQL
 Target Server Version : 80043
 File Encoding         : 65001

 Date: 28/07/2025 23:40:53
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for addresses
-- ----------------------------
DROP TABLE IF EXISTS `addresses`;
CREATE TABLE `addresses`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '地址唯一ID，主键',
  `user_id` bigint(0) NOT NULL COMMENT '所属用户的ID，外键',
  `recipient_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收件人姓名',
  `phone_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收件人电话',
  `street_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '街道地址',
  `city` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '城市',
  `state_province` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '省/市',
  `is_default` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否为默认地址',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '记录创建时间',
  `updated_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '记录最后更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `addresses_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '存储用户配送地址的表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of addresses
-- ----------------------------
INSERT INTO `addresses` VALUES (1, 1, 'admin', '1233445453', '村', '广州', '白云', 1, '2025-07-26 11:45:41', '2025-07-27 15:36:44');
INSERT INTO `addresses` VALUES (2, 12, 'AM', '13334454533', '村', '广州', '白云', 0, '2025-07-26 11:45:41', '2025-07-28 11:51:44');
INSERT INTO `addresses` VALUES (3, 12, 'AM', '13334454533', '村', '汕头', '白云', 1, '2025-07-26 11:45:41', '2025-07-27 15:36:44');

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '角色ID，主键',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称，必须唯一，如 ROLE_USER, ROLE_ADMIN',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '存储系统所有可用角色的表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of roles
-- ----------------------------
INSERT INTO `roles` VALUES (3, 'ROLE_ADMIN');
INSERT INTO `roles` VALUES (2, 'ROLE_ARTISAN');
INSERT INTO `roles` VALUES (4, 'ROLE_MODERATOR');
INSERT INTO `roles` VALUES (1, 'ROLE_USER');

-- ----------------------------
-- Table structure for user_profiles
-- ----------------------------
DROP TABLE IF EXISTS `user_profiles`;
CREATE TABLE `user_profiles`  (
  `user_id` bigint(0) NOT NULL COMMENT '用户ID，主键，同时也是外键，关联到users表',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
  `avatar_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户头像图片的URL',
  `bio` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '用户个人简介',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '记录创建时间',
  `updated_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '记录最后更新时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  CONSTRAINT `user_profiles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '存储用户个人资料的表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_profiles
-- ----------------------------
INSERT INTO `user_profiles` VALUES (1, 'admin-update', 'update.jpg', '更新后的简介', '2025-07-26 11:44:31', '2025-07-27 11:35:00');
INSERT INTO `user_profiles` VALUES (2, '美食家小王', 'default.jpg', '手工美食博主', '2025-07-26 12:12:46', '2025-07-26 12:30:37');
INSERT INTO `user_profiles` VALUES (3, '吃货乙', 'default.jpg', '一个吃货', '2025-07-26 12:29:23', '2025-07-26 12:29:43');
INSERT INTO `user_profiles` VALUES (4, 'pd6fvp4', 'default.jpg', '用户很懒，还没有设置简介哦~', '2025-07-27 07:49:16', '2025-07-27 07:49:16');
INSERT INTO `user_profiles` VALUES (6, 'kutqrx.i2x82', 'default.jpg', '用户很懒，还没有设置简介哦~', '2025-07-27 08:18:55', '2025-07-27 08:18:55');
INSERT INTO `user_profiles` VALUES (7, 'mawtue_fevfrz', 'default.jpg', '用户很懒，还没有设置简介哦~', '2025-07-27 08:20:34', '2025-07-27 08:20:34');
INSERT INTO `user_profiles` VALUES (8, 'thisam', 'default.jpg', '用户很懒，还没有设置简介哦~', '2025-07-27 09:21:52', '2025-07-27 09:21:52');
INSERT INTO `user_profiles` VALUES (12, 'thisam', 'default.jpg', '用户很懒，还没有设置简介哦~', '2025-07-28 08:37:11', '2025-07-28 08:37:11');
INSERT INTO `user_profiles` VALUES (14, 'thisam', 'default.jpg', '用户很懒，还没有设置简介哦~', '2025-07-28 08:42:15', '2025-07-28 08:42:15');

-- ----------------------------
-- Table structure for user_role_mapping
-- ----------------------------
DROP TABLE IF EXISTS `user_role_mapping`;
CREATE TABLE `user_role_mapping`  (
  `user_id` bigint(0) NOT NULL COMMENT '用户ID，外键',
  `role_id` int(0) NOT NULL COMMENT '角色ID，外键',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE,
  INDEX `role_id`(`role_id`) USING BTREE,
  CONSTRAINT `user_role_mapping_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_role_mapping_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户与角色的多对多映射关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_role_mapping
-- ----------------------------
INSERT INTO `user_role_mapping` VALUES (2, 1);
INSERT INTO `user_role_mapping` VALUES (3, 1);
INSERT INTO `user_role_mapping` VALUES (4, 1);
INSERT INTO `user_role_mapping` VALUES (6, 1);
INSERT INTO `user_role_mapping` VALUES (7, 1);
INSERT INTO `user_role_mapping` VALUES (8, 1);
INSERT INTO `user_role_mapping` VALUES (12, 1);
INSERT INTO `user_role_mapping` VALUES (14, 1);
INSERT INTO `user_role_mapping` VALUES (1, 3);

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '用户唯一ID，主键',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户邮箱，用于登录，必须唯一',
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '加盐哈希后的用户密码',
  `status` enum('ACTIVE','PENDING_VERIFICATION','SUSPENDED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PENDING_VERIFICATION' COMMENT '用户账户状态',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '记录创建时间',
  `updated_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '记录最后更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `email`(`email`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '存储用户核心认证信息的表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, '123456@123.com', '123456', 'ACTIVE', '2025-07-26 11:42:56', '2025-07-26 11:42:56');
INSERT INTO `users` VALUES (2, '222222@123.com', '123456', 'ACTIVE', '2025-07-26 12:11:52', '2025-07-26 12:11:52');
INSERT INTO `users` VALUES (3, '333333@123.com', '123456', 'ACTIVE', '2025-07-26 12:29:03', '2025-07-26 12:29:03');
INSERT INTO `users` VALUES (4, 'hm1nuy_i7t@163.com', 'Ojh2v8kGYqvSvdQ', 'ACTIVE', '2025-07-27 07:49:16', '2025-07-27 07:49:16');
INSERT INTO `users` VALUES (6, 'i1cuji_sn1@gmail.com', 'upWx3X1kmpAUe_G', 'ACTIVE', '2025-07-27 08:18:55', '2025-07-27 08:18:55');
INSERT INTO `users` VALUES (7, '132343423', '4OAZ94vzENiWah5', 'ACTIVE', '2025-07-27 08:20:34', '2025-07-27 08:20:34');
INSERT INTO `users` VALUES (8, 'jwui2v.i46@gmail.com', '123122342', 'ACTIVE', '2025-07-27 09:21:52', '2025-07-27 09:21:52');
INSERT INTO `users` VALUES (11, 'cheese@gmail.com', '123456', 'ACTIVE', '2025-07-28 06:35:56', '2025-07-28 06:35:56');
INSERT INTO `users` VALUES (12, 'thisam@gmail.com', '$2a$10$TG12D/Zu4Qfy1VxdHH.pAe21DUZtc66bb/tpedU7Htlxcsk4Jzjxy', 'ACTIVE', '2025-07-28 08:37:11', '2025-07-28 08:37:11');
INSERT INTO `users` VALUES (14, '1thisam@gmail.com', '$2a$10$En7wRvhhYLLL.FMRsyOwQu0vkTz.Yy/uynyNqHloSJM5iBP0USyYi', 'ACTIVE', '2025-07-28 08:42:15', '2025-07-28 08:42:15');

SET FOREIGN_KEY_CHECKS = 1;
