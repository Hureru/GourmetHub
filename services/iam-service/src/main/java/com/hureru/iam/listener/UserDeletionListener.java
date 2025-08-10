package com.hureru.iam.listener;

import com.hureru.iam.config.RabbitMQConfig;
import com.hureru.iam.service.IUsersService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author zheng
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserDeletionListener {

    private final IUsersService usersService;

    /**
     * 监听删除用户的队列
     * @param userId 从消息体中获取的用户ID
     * @param message RabbitMQ 的原始消息对象
     * @param channel AMQP 通道，用于手动确认消息
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleDeleteUser(String userId, Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            log.info("从MQ接收到删除用户请求, User ID: {}", userId);
            boolean isRemoved = usersService.removeById(userId);
            if (isRemoved) {
                log.info("用户成功删除, User ID: {}", userId);
            } else {
                log.warn("用户删除失败或用户不存在, User ID: {}", userId);
            }
            // 手动确认消息，通知 RabbitMQ 消息已成功处理
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("处理删除用户消息时发生异常, User ID: {}. 异常信息: {}", userId, e.getMessage());
            // 拒绝消息，并让其重新入队，以便后续重试
            // 第三个参数 requeue = true
            channel.basicNack(deliveryTag, false, true);
        }
    }
}
