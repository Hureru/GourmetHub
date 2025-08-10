package com.hureru.iam.config;

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
    public static final String QUEUE_NAME = "iam.delete.user.queue";
    public static final String ROUTING_KEY = "routing.user.delete";

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue deleteUserQueue() {
        return new Queue(QUEUE_NAME);
    }

    @Bean
    public Binding binding(Queue deleteUserQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(deleteUserQueue).to(directExchange).with(ROUTING_KEY);
    }
}
