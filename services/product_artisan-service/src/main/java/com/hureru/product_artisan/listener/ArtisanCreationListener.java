package com.hureru.product_artisan.listener;

import com.hureru.product_artisan.config.RabbitMQConfig;
import com.hureru.product_artisan.dto.ArtisanDTO;
import com.hureru.product_artisan.service.IArtisanService;
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
public class ArtisanCreationListener {

    private final IArtisanService artisanService;

    /**
     * 监听创建商家的队列
     * @param artisanDTO 从消息体中反序列化得到的商家数据
     * @param message RabbitMQ 的原始消息对象
     * @param channel AMQP 通道，用于手动确认消息
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleCreateArtisan(ArtisanDTO artisanDTO, Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            log.info("从MQ接收到创建商家请求, User ID: {}", artisanDTO.getId());
            // 调用业务逻辑层创建商家
            artisanService.saveArtisan(artisanDTO);
            log.info("商家信息成功创建, User ID: {}", artisanDTO.getId());

            // 手动确认消息，通知 RabbitMQ 消息已成功处理
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            // 如果是已存在的商家（例如消息重复消费），也应该确认消息，避免无限重试
            // 您可以在这里添加更精细的异常判断逻辑
            if (e instanceof org.springframework.dao.DuplicateKeyException) {
                log.warn("商家信息已存在 (可能为重复消息), User ID: {}. 消息将被确认。", artisanDTO.getId());
                channel.basicAck(deliveryTag, false);
            } else {
                log.error("处理创建商家消息时发生异常, User ID: {}. 异常信息: {}", artisanDTO.getId(), e.getMessage());
                // 拒绝消息，并让其重新入队，以便后续重试
                channel.basicNack(deliveryTag, false, true);
            }
        }
    }
}
