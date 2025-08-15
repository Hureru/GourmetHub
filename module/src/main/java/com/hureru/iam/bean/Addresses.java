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
import lombok.experimental.Accessors;

/**
 * <p>
 * 存储用户配送地址的表
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("addresses")
@ApiModel(value="Addresses对象", description="存储用户配送地址的表")
public class Addresses implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "地址唯一ID，主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "所属用户的ID，外键")
    private Long userId;

    @ApiModelProperty(value = "收件人姓名")
    private String recipientName;

    @ApiModelProperty(value = "收件人电话")
    private String phoneNumber;

    @ApiModelProperty(value = "街道地址")
    private String streetAddress;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "省/市")
    private String stateProvince;

    @ApiModelProperty(value = "是否为默认地址")
    private Boolean isDefault;

    @ApiModelProperty(value = "记录创建时间")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "记录最后更新时间")
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "{" +
                "recipientName:\"" + recipientName + '"' +
                ", phoneNumber:\"" + phoneNumber + '"' +
                ", stateProvince:\"" + stateProvince + '"' +
                ", city:\"" + city + '"' +
                ", streetAddress:\"" + streetAddress + '"' +
                '}';
    }
}
