package com.hureru.iam.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 存储用户核心认证信息的表
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@Data
@NoArgsConstructor
@TableName("users")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Users对象", description="存储用户核心认证信息的表")
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户唯一ID，主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户邮箱，用于登录，必须唯一")
    private String email;

    @ApiModelProperty(value = "加盐哈希后的用户密码")
    private String passwordHash;

    @ApiModelProperty(value = "用户账户状态")
    private Status status;

    @ApiModelProperty(value = "是否商家")
    private Boolean isArtisan;

    @ApiModelProperty(value = "记录创建时间")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "记录最后更新时间")
    private LocalDateTime updatedAt;

    public enum Status {
        ACTIVE,
        PENDING_VERIFICATION,
        SUSPENDED
    }

    public Users(String email, String password) {
        this.email = email;
        this.passwordHash = password;
    }
}
