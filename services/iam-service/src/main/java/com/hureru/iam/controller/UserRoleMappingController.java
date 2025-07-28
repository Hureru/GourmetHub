package com.hureru.iam.controller;


import com.hureru.iam.RoleEnum;
import com.hureru.iam.service.IUserRoleMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户与角色的多对多映射关系表 前端控制器
 * </p>
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
     * 受保护接口，仅管理员，修改用户权限为管理员
     */
    @PutMapping("/users/{id}/admin")
    public String updateUserRoleAdmin(/*@AuthenticationPrincipal Jwt jwt,*/ @PathVariable Long id) {
        // TODO 从JWT中获取用户ID
        Long userId = 1L;
        userRoleMappingService.updateUserRole(id, userId, RoleEnum.ROLE_ADMIN);
        return "success";
    }

    /**
     * 受保护接口，仅管理员，修改用户权限为审核
     */
    @PutMapping("/users/{id}/moderator")
    public String updateUserRoleModerator(/*@AuthenticationPrincipal Jwt jwt,*/ @PathVariable Long id) {
        // TODO 从JWT中获取用户ID
        Long userId = 1L;
        userRoleMappingService.updateUserRole(id, userId, RoleEnum.ROLE_MODERATOR);
        return "success";
    }
}
