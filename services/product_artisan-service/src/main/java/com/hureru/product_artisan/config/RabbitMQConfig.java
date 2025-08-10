package com.hureru.product_artisan.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zheng
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "gourmethub.direct";
    public static final String QUEUE_NAME = "product.create.artisan.queue";
    public static final String ROUTING_KEY = "routing.artisan.create";

    @Bean
    public DirectExchange directExchange() {
        // 声明交换机，如果已存在则不会重复创建
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue createArtisanQueue() {
        // 声明队列
        return new Queue(QUEUE_NAME);
    }

    @Bean
    public Binding bindingCreateArtisan(Queue createArtisanQueue, DirectExchange directExchange) {
        // 将队列绑定到交换机，并指定路由键
        return BindingBuilder.bind(createArtisanQueue).to(directExchange).with(ROUTING_KEY);
    }
}
