package com.hureru.order.service;

import com.hureru.order.bean.Orders;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hureru.order.dto.CreateOrderDirectlyDTO;
import com.hureru.order.dto.CreateOrderFromCartDTO;
import com.hureru.order.dto.OrderTransactionPayload;

/**
 * <p>
 * 订单主表 服务类
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
public interface IOrdersService extends IService<Orders> {
    String createOrderFromCart(Long userId, CreateOrderFromCartDTO dto);

    String createOrderDirectly(Long userId, CreateOrderDirectlyDTO dto);

    Orders getOrderByOrderId(String orderSn);
    Orders getOrderFromUser(Long userId, String orderId);

    // 本地事务方法
    boolean executeCreateOrderTransaction(OrderTransactionPayload payload);

    // 订单取消方法
    void cancelOrder(String orderId);
}
