package com.hureru.recipe_content.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * JWT 令牌传播器，用于在微服务间传递 JWT 令牌
 * 实现 RequestInterceptor 接口，在 Feign 请求发送前自动添加 JWT 令牌到请求头
 * @author zheng
 */
@Component
public class JwtTokenPropagator implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            String token = jwtAuth.getToken().getTokenValue();
            template.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }
    }
}
