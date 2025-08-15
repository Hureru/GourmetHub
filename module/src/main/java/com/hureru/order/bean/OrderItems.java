package com.hureru.order.bean;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.hureru.order.OrderStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 订单中的具体商品项
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("order_items")
@ApiModel(value="OrderItems对象", description="订单中的具体商品项")
public class OrderItems implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单项ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "所属订单ID")
    private Long orderId;

    @ApiModelProperty(value = "商品ID (引用MongoDB中的产品ID)")
    private String productId;

    @ApiModelProperty(value = "商品信息快照 (名称, 图片等)，JSON格式")
    private String productSnapshot;

    @ApiModelProperty(value = "购买数量")
    private Integer quantity;

    @ApiModelProperty(value = "下单时的单价快照")
    private BigDecimal priceAtPurchase;

    @ApiModelProperty(value = "商品状态")
    private OrderStatus shippingStatus;

}
