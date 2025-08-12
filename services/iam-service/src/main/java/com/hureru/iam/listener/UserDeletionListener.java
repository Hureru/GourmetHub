package com.hureru.iam.listener;

import com.hureru.iam.service.IUsersService;
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
        consumerGroup = "iam-user-deletion-consumer-group",
        topic = "TOPIC_ARTISAN_DELETE"
)
public class UserDeletionListener implements RocketMQListener<String> { // 消息体是 String 类型的用户ID

    private final IUsersService usersService;

    @Override
    public void onMessage(String userId) {
        try {
            log.info("从MQ接收到删除用户请求, User ID: {}", userId);

            // 调用业务逻辑层删除用户
            boolean isRemoved = usersService.removeById(userId);

            if (isRemoved) {
                log.info("用户账号成功删除, User ID: {}", userId);
            } else {
                // 这可能是因为消息重复消费，或者在消息到达前用户已被删除
                // 这种情况是正常的，记录警告即可，不需要重试
                log.warn("用户账号删除失败或已不存在, User ID: {}", userId);
            }
        } catch (Exception e) {
            // 发生其他未知异常（如数据库连接失败）
            log.error("消费删除用户消息时发生异常, User ID: {}. 将会重试. 错误: {}", userId, e.getMessage());
            // 抛出异常，RocketMQ 会根据配置进行重试
            throw new RuntimeException("消费失败，触发重试", e);
        }
    }
}
