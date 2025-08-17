package com.hureru.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 全局Filter 只需要放到容器中，不需要任何配置就能生效
 * @author zheng
 */
@Slf4j
@Component
public class RtGlobalFilter implements GlobalFilter, Ordered {


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String uri = request.getURI().getPath();
        long start = System.currentTimeMillis();
        log.info("请求【{}】开始：时间：{}", uri, start);
        //==================以上是前置逻辑==================

        return chain.filter(exchange)
                .doFinally((result)->{
                    //==================以下是后置逻辑==================
                    long end = System.currentTimeMillis();
                    log.info("请求【{}】结束：时间：{}，耗时：{}ms", uri, end, end - start);
                });
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
