package com.hureru.iam.bean;

import com.baomidou.mybatisplus.annotation.TableField;
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
 * 通知类型表
 * </p>
 *
 * @author zheng
 * @since 2025-08-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("notification_types")
@ApiModel(value="NotificationTypes对象", description="通知类型表")
public class NotificationTypes implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "通知类型名称，例如 '订单状态更新'")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "关于此通知类型的详细描述")
    @TableField("description")
    private String description;


}
