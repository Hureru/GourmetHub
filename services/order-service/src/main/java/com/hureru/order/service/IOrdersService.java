package com.hureru.order.service;

import com.hureru.common.PaginationData;
import com.hureru.order.OrderStatus;
import com.hureru.order.bean.Orders;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hureru.order.dto.CreateOrderDirectlyDTO;
import com.hureru.order.dto.CreateOrderFromCartDTO;
import com.hureru.order.dto.OrderDTO;
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
    PaginationData<OrderDTO> getAllOrders(OrderStatus status, int page, int size);

    /**
     * 根据商家ID获取包含该商家商品的所有订单，返回OrderDTO格式
     * @param page 页码
     * @param size 每页大小
     * @return 分页订单数据(OrderDTO格式)
     */
    PaginationData<OrderDTO> getOrdersWithArtisanItemsByStatus(OrderStatus status, int page, int size);

    PaginationData<OrderDTO> getUserOrders(Long userId, int page, int size);

    String createOrderFromCart(Long userId, CreateOrderFromCartDTO dto);

    String createOrderDirectly(Long userId, CreateOrderDirectlyDTO dto);

    Orders getOrderByOrderId(String orderSn);
    OrderDTO getOrderFromUser(Long userId, String orderId);

    // 本地事务方法
    boolean executeCreateOrderTransaction(OrderTransactionPayload payload);

    // 订单取消方法
    void cancelOrder(String orderId);
}
