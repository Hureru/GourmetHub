package com.hureru.product_artisan.listener;

import com.hureru.product_artisan.dto.ArtisanDTO;
import com.hureru.product_artisan.service.IArtisanService;
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
// 定义消费者组和订阅的 Topic
@RocketMQMessageListener(
        consumerGroup = "product-artisan-consumer-group",
        topic = "TOPIC_ARTISAN_CREATE"
)
public class ArtisanCreationListener implements RocketMQListener<ArtisanDTO> {

    private final IArtisanService artisanService;

    @Override
    public void onMessage(ArtisanDTO artisanDTO) {
        try {
            log.info("从MQ接收到创建商家请求, User ID: {}", artisanDTO.getId());
            // 调用业务逻辑层创建商家
            artisanService.saveArtisan(artisanDTO);
            log.info("商家信息成功创建, User ID: {}", artisanDTO.getId());
        } catch (Exception e) {
            log.error("消费创建商家消息失败, User ID: {}. 错误: {}", artisanDTO.getId(), e.getMessage());
            // 抛出异常，RocketMQ 会根据配置进行重试
            throw new RuntimeException("消费失败，触发重试", e);
        }
    }
}
