package com.hureru.product_artisan.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hureru.order.dto.OrderTransactionPayload;
import com.hureru.order.dto.StockDeductionRequest;
import com.hureru.product_artisan.service.IProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author zheng
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = "TX_ORDER_TOPIC",
        consumerGroup = "stock_consumer_group"
)
public class StockDeductionListener implements RocketMQListener<MessageExt> {

    private final IProductService productService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(MessageExt message) {
        try {
            // 从事务消息中解析出真正的业务数据
            OrderTransactionPayload payload = objectMapper.readValue(message.getBody(), OrderTransactionPayload.class);
            log.info("收到订单创建消息，准备扣减库存, orderId: {}", payload.getOrderSn());

            StockDeductionRequest request = new StockDeductionRequest(payload.getOrderSn(), payload.getItems());

            productService.deductStock(request);

        } catch (IOException e) {
            log.error("解析订单消息失败", e);
            // 考虑是否需要重试
        } catch (Exception e) {
            log.error("处理库存扣减时发生未知异常", e);
            // 业务异常已在service层处理（发送补偿消息），这里捕获其他运行时异常
            // 默认情况下，抛出异常会导致消息重新消费
            throw e;
        }
    }
}
