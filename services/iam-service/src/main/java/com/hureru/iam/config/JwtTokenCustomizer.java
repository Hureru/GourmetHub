package com.hureru.iam.config;

import com.hureru.iam.bean.Users;
import com.hureru.iam.oauth.SecurityUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JWT 自定义器，用于在 JWT 中添加自定义声明
 * @author zheng
 */
@Component
public class JwtTokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    final Map<String, List<String>> roleToScopes = new HashMap<>()
    {
        {
            put("ROLE_ADMIN",
                    List.of("role", "userprofile",
                    "users.status.update", "users.delete",
                    "artisan.active", "artisans.pendings",
                    "artisans.delete", "products.approve",
                    "products.get","orders.list"));
            put("ROLE_MODERATOR",
                    List.of("artisan.active", "artisans.pendings", "products.approve"));
            put("ROLE_ARTISAN",
                    List.of("artisans.get", "artisans.update",
                    "products.add","products.artisan.get",
                    "products.update", "products.delete", "orders.artisan",
                            "recipe.create","recipe.update",
                            "comment.create"));
            put("ROLE_USER",
                    List.of("address", "userprofile",
                    "artisans.get", "products.view",
                            "cart-items.update","cart-items.create","cart-items.delete","cart.get",
                            "orders.create", "orders.view","order.get",
                            "users.pending",
                            "artisan.isEffective",
                            "recipe.create","recipe.update",
                            "comment.create"));
        }
    };

    /**
     * 自定义 JWT 的声明
     * @param context JWT 编码上下文
     */
    @Override
    public void customize(JwtEncodingContext context) {
        Object principal = context.getPrincipal().getPrincipal();

        // 现在 Principal 是我们自定义的 SecurityUser 类型
        if (principal instanceof SecurityUser securityUser) {
            // 从 SecurityUser 中直接获取包装好的 Users 对象
            Users user = securityUser.getUser();

            context.getClaims().claim("user_id", user.getId());

            // 从 SecurityUser 中直接获取权限信息，无需查库！
            securityUser.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .ifPresent(roleName -> {
                        List<String> scopes = roleToScopes.get(roleName);
                        if (scopes != null) {
                            context.getClaims().claim("scope", scopes);
                            context.getClaims().claim("isArtisan", "ROLE_ARTISAN".equals(roleName));
                        }
                    });
        }
    }
}
