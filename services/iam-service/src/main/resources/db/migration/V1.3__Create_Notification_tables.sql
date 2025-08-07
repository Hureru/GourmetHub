-- -----------------------------------------------------
-- 表 `Notification_Channels`
-- 描述：存储所有可用的通知渠道，如邮件、App推送等。
-- -----------------------------------------------------
CREATE TABLE `notification_channels` (
     `id` INT NOT NULL AUTO_INCREMENT,
     `name` VARCHAR(100) NOT NULL COMMENT '渠道名称，例如 ''邮件''、''App推送''、''短信''',
     PRIMARY KEY (`id`),
     UNIQUE INDEX `name_UNIQUE` (`name` ASC)
) ENGINE=InnoDB COMMENT='通知渠道表';


-- -----------------------------------------------------
-- 表 `Notification_Types`
-- 描述：存储所有通知的类型，如订单更新、营销活动等。
-- -----------------------------------------------------
CREATE TABLE `notification_types` (
      `id` INT NOT NULL AUTO_INCREMENT,
      `name` VARCHAR(100) NOT NULL COMMENT '通知类型名称，例如 ''订单状态更新''',
      `description` TEXT NULL COMMENT '关于此通知类型的详细描述',
      PRIMARY KEY (`id`),
      UNIQUE INDEX `name_UNIQUE` (`name` ASC)
) ENGINE=InnoDB COMMENT='通知类型表';


-- -----------------------------------------------------
-- 表 `User_Notification_Preferences`
-- 描述：存储每个用户对每种通知类型在每个渠道上的偏好设置。
-- -----------------------------------------------------
CREATE TABLE `user_notification_preferences` (
     `id` INT NOT NULL AUTO_INCREMENT,
     `user_id` BIGINT NOT NULL COMMENT '外键，关联到 Users 表的 id',
     `notification_type_id` INT NOT NULL COMMENT '外键，关联到 Notification_Types 表的 id',
     `notification_channel_id` INT NOT NULL COMMENT '外键，关联到 Notification_Channels 表的 id',
     `is_enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '布尔值，1 表示开启，0 表示关闭',
     PRIMARY KEY (`id`),
     INDEX `fk_user_id_idx` (`user_id` ASC),
     INDEX `fk_notification_type_id_idx` (`notification_type_id` ASC),
     INDEX `fk_notification_channel_id_idx` (`notification_channel_id` ASC),
-- 创建一个联合唯一索引，确保每个用户对同一种类型和渠道的设置只有一条记录
     UNIQUE INDEX `unique_user_type_channel` (`user_id`, `notification_type_id`, `notification_channel_id`),
-- 添加外键约束
     CONSTRAINT `fk_preferences_to_users`
         FOREIGN KEY (`user_id`)
             REFERENCES `users` (`id`)
             ON DELETE CASCADE
             ON UPDATE CASCADE,
     CONSTRAINT `fk_preferences_to_types`
         FOREIGN KEY (`notification_type_id`)
             REFERENCES `notification_types` (`id`)
             ON DELETE CASCADE
             ON UPDATE CASCADE,
     CONSTRAINT `fk_preferences_to_channels`
         FOREIGN KEY (`notification_channel_id`)
             REFERENCES `notification_channels` (`id`)
             ON DELETE CASCADE
             ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='用户通知偏好设置表';


-- -----------------------------------------------------
-- 插入示例数据
-- -----------------------------------------------------

-- 插入通知渠道
INSERT INTO `notification_channels` (`id`, `name`) VALUES
                                                       (1, '邮件'),
                                                       (2, 'App推送'),
                                                       (3, '短信');

-- 插入通知类型
INSERT INTO `notification_types` (`id`, `name`, `description`) VALUES
                                                                   (1, '订单状态更新', '当您的订单状态发生变化时（如已发货、已签收），我们会通知您。'),
                                                                   (2, '营销活动', '向您推荐最新的优惠活动和商品。'),
                                                                   (3, '系统公告', '重要的服务更新或平台公告。');

-- 插入用户偏好设置示例
-- 假设所有通知默认开启，我们在这里只插入一些用户主动关闭的设置
INSERT INTO `user_notification_preferences` (`user_id`, `notification_type_id`, `notification_channel_id`, `is_enabled`) VALUES
-- 用户1 (zhangsan) 的设置
(1, 1, 1, 1), -- 开启: 订单状态更新 - 邮件
(1, 1, 2, 1), -- 开启: 订单状态更新 - App推送
(1, 1, 3, 0), -- 关闭: 订单状态更新 - 短信
(1, 2, 1, 0), -- 关闭: 营销活动 - 邮件
(1, 2, 2, 1), -- 开启: 营销活动 - App推送
(1, 3, 2, 1), -- 开启: 系统公告 - App推送

-- 用户2 (lisi) 的设置
(2, 1, 1, 1), -- 开启: 订单状态更新 - 邮件
(2, 2, 1, 0), -- 关闭: 营销活动 - 邮件
(2, 2, 2, 0); -- 关闭: 营销活动 - App推送