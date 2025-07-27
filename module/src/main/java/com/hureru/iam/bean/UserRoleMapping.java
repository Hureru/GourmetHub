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
 * 用户与角色的多对多映射关系表
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_role_mapping")
@ApiModel(value="UserRoleMapping对象", description="用户与角色的多对多映射关系表")
public class UserRoleMapping implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID，外键")
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    @ApiModelProperty(value = "角色ID，外键")
    private Integer roleId;


    public UserRoleMapping(Long userId, int id) {
        this.setUserId(userId);
        this.setRoleId(id);
    }
}
