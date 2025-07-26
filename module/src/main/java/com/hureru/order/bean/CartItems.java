package com.hureru.order.bean;

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
 * 购物车中的商品项
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("cart_items")
@ApiModel(value="CartItems对象", description="购物车中的商品项")
public class CartItems implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "购物车项ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "所属购物车ID")
    private Long cartId;

    @ApiModelProperty(value = "商品ID (引用MongoDB中的产品ID)")
    private String productId;

    @ApiModelProperty(value = "商品数量")
    private Integer quantity;

    @ApiModelProperty(value = "添加时间")
    private LocalDateTime addedAt;


}
