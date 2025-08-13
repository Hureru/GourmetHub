package com.hureru.order.config;

import com.hureru.order.utils.OrderIdUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderIdConfig {

    @Value("${order-id.worker-id}")
    private long workerId;

    @Value("${order-id.datacenter-id}")
    private long datacenterId;

    @Bean
    public OrderIdUtil orderIdUtil() {
        return new OrderIdUtil(workerId, datacenterId);
    }
}

