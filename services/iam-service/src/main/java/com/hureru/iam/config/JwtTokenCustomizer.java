package com.hureru.iam.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hureru.common.exception.BusinessException;
import com.hureru.iam.RoleEnum;
import com.hureru.iam.bean.UserRoleMapping;
import com.hureru.iam.bean.Users;
import com.hureru.iam.service.IUserRoleMappingService;
import com.hureru.iam.service.IUsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequiredArgsConstructor
public class JwtTokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    private final IUsersService usersService;
    private final IUserRoleMappingService userRoleMappingService;

    final Map<String, List<String>> roleToScopes = new HashMap<>()
    {
        {
            put("ROLE_ADMIN", List.of("role", "userprofile",
                    "users.status.update", "users.delete",
                    "artisan.active", "artisans.pendings",
                    "artisans.delete"));
            put("ROLE_MODERATOR", List.of("artisan.active", "artisans.pendings"));
            put("ROLE_ARTISAN", List.of("artisans.update"));
            put("ROLE_USER", List.of("address", "userprofile"));
        }
    };

    /**
     * 自定义 JWT 的声明
     * @param context JWT 编码上下文
     */
    @Override
    public void customize(JwtEncodingContext context) {
        // 1. 获取当前认证的用户名
        String username = context.getPrincipal().getName();

        // 2. 根据用户名查询用户实体
        Users user = usersService.getOne(new QueryWrapper<Users>().eq("email", username));

        // 3. 如果用户存在，则将 user_id 添加到 JWT 的 claims 中
        if (user != null) {
            context.getClaims().claim("user_id", user.getId());
            UserRoleMapping userRoleMapping = userRoleMappingService.getById(user.getId());
            RoleEnum role = RoleEnum.getRole(userRoleMapping.getRoleId());
            if (role == null){
                throw new BusinessException(404, "用户角色不存在");
            }
            context.getClaims().claim("scope", roleToScopes.get(role.name()));
        }
    }
}
