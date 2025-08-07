package com.hureru.iam.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户通知偏好设置表
 * </p>
 *
 * @author zheng
 * @since 2025-08-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_notification_preferences")
@ApiModel(value="UserNotificationPreferences对象", description="用户通知偏好设置表")
public class UserNotificationPreferences implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "外键，关联到 Users 表的 id")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty(value = "外键，关联到 Notification_Types 表的 id")
    @TableField("notification_type_id")
    private Integer notificationTypeId;

    @ApiModelProperty(value = "外键，关联到 Notification_Channels 表的 id")
    @TableField("notification_channel_id")
    private Integer notificationChannelId;

    @ApiModelProperty(value = "布尔值，1 表示开启，0 表示关闭")
    @TableField("is_enabled")
    private Boolean isEnabled;


}
