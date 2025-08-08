package com.hureru.iam.controller;


import com.hureru.common.Response;
import com.hureru.common.utils.JwtUtil;
import com.hureru.iam.RoleEnum;
import com.hureru.iam.service.IUserRoleMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

/**
 * 用户与角色的多对多映射关系表 前端控制器
 *
 * @author zheng
 * @since 2025-07-26
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@PreAuthorize("hasAuthority('SCOPE_role')")
public class UserRoleMappingController {
    private final IUserRoleMappingService userRoleMappingService;
    /**
     * 受保护接口，仅管理员，修改用户权限为 管理员
     * @param jwt 用户令牌
     */
    @PatchMapping("/users/{id}/admin")
    public Response updateUserRoleAdmin(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        // 从JWT中获取用户ID
        Long userId = JwtUtil.getUserIdFromJwt(jwt);
        userRoleMappingService.updateUserRole(userId, id, RoleEnum.ROLE_ADMIN);
        return Response.ok();
    }

    /**
     * 受保护接口，仅管理员，修改用户权限为 审核
     * @param jwt 用户令牌
     */
    @PatchMapping("/users/{id}/moderator")
    public Response updateUserRoleModerator(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        // 从JWT中获取用户ID
        Long userId = JwtUtil.getUserIdFromJwt(jwt);
        userRoleMappingService.updateUserRole(userId, id, RoleEnum.ROLE_MODERATOR);
        return Response.ok();
    }
}
