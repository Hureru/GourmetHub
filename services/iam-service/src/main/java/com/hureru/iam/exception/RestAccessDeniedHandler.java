package com.hureru.iam.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hureru.common.R;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 自定义访问拒绝处理器，用于处理权限不足的情况。
 * 返回 403 Forbidden 错误和统一的 JSON 响应体。
 *
 * @author zheng
 */
@Component
@RequiredArgsConstructor
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        // 使用 R 对象封装错误信息
        R errorResponse = R.error(403, "无权访问此资源: " + accessDeniedException.getMessage());
        // 将 R 对象序列化为 JSON 字符串并写入响应
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
