package com.hureru.order.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author zheng
 */
@Data
public class CreateOrderFromCartDTO {
    @NotEmpty(message = "请选择要结算的商品")
    private List<Long> cartItemIds;

    @NotNull(message = "收货地址ID不能为空")
    private Long addressId;
}
