package com.hureru.iam.controller;


import com.hureru.common.R;
import com.hureru.iam.bean.UserProfiles;
import com.hureru.iam.dto.UserProfileDTO;
import com.hureru.iam.service.IUserProfilesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 存储用户个人资料的表 前端控制器
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@PreAuthorize("hasAuthority('SCOPE_userprofile')")
public class UserProfilesController {
    private final IUserProfilesService userProfilesService;
    /**
     * 受保护接口, 获取当前认证用户的完整个人资料
     * @return {@code 200 OK} 返回个人资料
     * {@code 401 Unauthorized} 令牌无效或缺失
     */
    @GetMapping("/users/me")
    public R me(/*@AuthenticationPrincipal Jwt jwt,*/) {
        // TODO 从JWT中获取用户ID
        Long userId = 1L;
        // 实现获取个人资料逻辑
        UserProfiles profile = userProfilesService.getById(userId);
        return R.ok("获取成功", profile);
    }

    /**
     * 受保护接口, 更新当前认证用户的个人资料
     * @param userProfile 需要更新的资料
     * @return {@code 200 OK} 返回 success
     * {@code 401 Unauthorized} 令牌无效或缺失
     */
    @PutMapping("/users/me")
    public R updateMe(/*@AuthenticationPrincipal Jwt jwt,*/ @Valid @RequestBody UserProfileDTO userProfile) {
        // TODO 从JWT中获取用户ID
        Long userId = 1L;
        // 更新个人资料
        userProfilesService.updateUserByFields(userId, userProfile);
        return R.ok(200);
    }

}
