package com.hureru.order;

/**
 * 订单状态枚举
 * @author zheng
 */
public enum OrderStatus {
    // 待处理，事务消息已发送，等待消费者确认
    PENDING,
    // 待支付，库存扣减成功 (此项目简化，暂不实现支付)
    AWAITING_PAYMENT,
    // 已支付
    PAID,
    // 已发货
    SHIPPED,
    // 已完成
    COMPLETED,
    // 已取消（库存扣减失败或用户取消）
    CANCELLED,
    // 已退款
    REFUNDED
}
