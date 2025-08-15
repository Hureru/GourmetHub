package com.hureru.order.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hureru.order.OrderStatus;
import com.hureru.order.bean.Orders;
import com.hureru.order.dto.OrderTransactionPayload;
import com.hureru.order.service.IOrdersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;

/**
 * @author zheng
 */
@Slf4j
@RequiredArgsConstructor
@RocketMQTransactionListener()
public class OrderTransactionListener implements RocketMQLocalTransactionListener {

    private final IOrdersService ordersService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 执行本地事务
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        log.info("开始执行本地事务...");
        OrderTransactionPayload payload = (OrderTransactionPayload) arg;
        try {
            boolean result = ordersService.executeCreateOrderTransaction(payload);
            if (result) {
                log.info("本地事务执行成功, 提交消息, orderId: {}", payload.getOrderSn());
                return RocketMQLocalTransactionState.COMMIT;
            } else {
                log.warn("本地事务执行失败, 回滚消息, orderId: {}", payload.getOrderSn());
                return RocketMQLocalTransactionState.ROLLBACK;
            }
        } catch (Exception e) {
            log.error("本地事务执行异常, 回滚消息, orderId: {}", payload.getOrderSn(), e);
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    /**
     * 检查本地事务状态（回查）
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        // 从消息体中解析出 orderId
        String orderId = null;
        try {
            // 注意：回查时 arg 是 null，需要从 msg body 解析
            OrderTransactionPayload payload = objectMapper.readValue((byte[]) msg.getPayload(), OrderTransactionPayload.class);
            orderId = payload.getOrderSn();
            log.info("开始回查本地事务状态, orderId: {}", orderId);

            Orders order = ordersService.getOrderByOrderId(orderId);

            if (order != null && order.getStatus() != OrderStatus.CANCELLED) {
                // 只要订单存在且不是取消状态，就认为本地事务是成功的
                log.info("回查结果: 事务成功, 提交, orderId: {}", orderId);
                return RocketMQLocalTransactionState.COMMIT;
            } else {
                log.warn("回查结果: 事务失败, 回滚, orderId: {}", orderId);
                return RocketMQLocalTransactionState.ROLLBACK;
            }
        } catch (Exception e) {
            log.error("回查本地事务状态异常, orderId: {}", orderId, e);
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }
}
