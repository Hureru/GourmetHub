package com.hureru.order.mq;

import com.hureru.order.service.IOrdersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @author zheng
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(topic = "COMPENSATE_STOCK_TOPIC", consumerGroup = "compensate_stock_consumer_group")
public class StockCompensationListener implements RocketMQListener<String> {

    private final IOrdersService ordersService;

    @Override
    public void onMessage(String orderId) {
        log.warn("收到库存扣减失败的补偿消息, 准备取消订单, orderId: {}", orderId);
        try {
            ordersService.cancelOrder(orderId);
        } catch (Exception e) {
            log.error("处理库存补偿消息失败，需要人工干预, orderId: {}", orderId, e);
            // 此处可以加入重试逻辑或记录到失败表
        }
    }
}
