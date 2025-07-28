package com.hureru.common.utils;

import com.hureru.common.exception.BusinessException;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * @author zheng
 */
public class JwtUtil {
    public static Long getUserIdFromJwt(Jwt jwt) {
        // 从 JWT 中直接获取 user_id
        // 使用 getClaim(String) 并进行安全的类型转换，以获得最好的兼容性
        Object userIdObject = jwt.getClaim("user_id");
        Long userId = null;
        if (userIdObject instanceof Number) {
            userId = ((Number) userIdObject).longValue();
        }
        // 如果 user_id 不存在（理论上不应该发生），进行安全处理
        if (userId == null) {
            throw new BusinessException(400, "USER_ID_NOT_FOUND", "用户 ID 未找到");
        }
        return userId;
    }
}
