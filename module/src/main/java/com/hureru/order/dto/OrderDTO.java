package com.hureru.order.dto;

import com.hureru.order.OrderStatus;
import com.hureru.order.bean.OrderItems;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zheng
 */
@Data
public class OrderDTO {
    private Long id;

    private String orderSn;

    private Long userId;

    private BigDecimal totalAmount;

    private String paymentMethod;

    private OrderStatus status;

    private String shippingAddress;

    private List<OrderItems> orderItems;

    private LocalDateTime paidAt;

    private LocalDateTime shippedAt;

    private LocalDateTime deliveredAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
