package com.hureru.gateway.filter;


import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

/**
 * @author zheng
 */
@Component
public class InternalApiFilter implements GlobalFilter, Ordered {

    // 使用正则表达式匹配多个路径模式
    private static final Pattern BLOCKED_PATH_PATTERN = Pattern.compile(
            "^/.*/api/v1/internal(?:/.*)?"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 使用正则表达式检查路径是否匹配
        if (BLOCKED_PATH_PATTERN.matcher(path).matches()) {
            // 拦截处理逻辑
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // 设置过滤器优先级，数值越小优先级越高
        return -1;
    }
}
