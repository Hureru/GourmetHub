package com.hureru.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author zheng
 */
@Data
public class CreateOrderDirectlyDTO {
    @NotNull(message = "商品ID不能为空")
    private String productId;

    @NotNull(message = "商品数量不能为空")
    @Min(value = 1, message = "商品数量至少为1")
    private Integer quantity;

    @NotNull(message = "收货地址ID不能为空")
    private Long addressId;
}
