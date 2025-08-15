package com.hureru.order.dto;

import com.hureru.order.dto.StockDeductionRequest.OrderItemDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用于在事务消息中传递参数的载体
 * @author zheng
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderTransactionPayload {
    private Long userId;
    private Long addressId;
    private List<OrderItemDTO> items;
    // 这个标志位现在表示“清空已结算的购物车项”
    private boolean clearCart;
    // 用于传递需要删除的购物车项ID
    private List<Long> cartItemIdsToDelete;
    private String orderSn;
}
