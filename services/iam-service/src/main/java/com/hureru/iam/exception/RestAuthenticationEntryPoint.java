package com.hureru.iam.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hureru.common.R;
import com.hureru.common.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 自定义认证入口点，用于处理认证失败（如令牌无效、缺失）的情况。
 * 返回 401 Unauthorized 错误和统一的 JSON 响应体。
 *
 * @author zheng
 */
@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        // 使用 R 对象封装错误信息
        Response errorResponse = Response.error(401, "令牌无效或缺失: " + authException.getMessage());
        // 将 R 对象序列化为 JSON 字符串并写入响应
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
