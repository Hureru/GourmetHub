package com.hureru.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author zheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockDeductionRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String orderSn;
    private List<OrderItemDTO> items;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderItemDTO implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        private String productId;
        private Integer quantity;
    }
}
