package com.hureru.iam.controller;


import com.hureru.common.R;
import com.hureru.common.Response;
import com.hureru.common.utils.JwtUtil;
import com.hureru.iam.bean.UserProfiles;
import com.hureru.iam.dto.UserProfileDTO;
import com.hureru.iam.service.IUserProfilesService;
import com.hureru.recipe_content.bean.Recipe;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;


/**
 * 存储用户个人资料的表 前端控制器
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
     * @param jwt 用户令牌
     * @return {@code 200 OK} 返回个人资料
     * {@code 401 Unauthorized} 令牌无效或缺失
     */
    @GetMapping("/users/me")
    public R<UserProfiles> me(@AuthenticationPrincipal Jwt jwt) {
        // 从JWT中获取用户ID
        Long userId = JwtUtil.getUserIdFromJwt(jwt);
        // 实现获取个人资料逻辑
        UserProfiles profile = userProfilesService.getById(userId);
        return R.ok("获取成功", profile);
    }

    /**
     * 受保护接口, 更新当前认证用户的个人资料
     * @param jwt 用户令牌
     * @param userProfile 需要更新的资料
     * @return {@code 200 OK} 返回 ok
     * {@code 401 Unauthorized} 令牌无效或缺失
     */
    @PatchMapping("/users/me")
    public Response updateMe(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UserProfileDTO userProfile) {
        // 从JWT中获取用户ID
        Long userId = JwtUtil.getUserIdFromJwt(jwt);
        // 更新个人资料
        userProfilesService.updateUserByFields(userId, userProfile);
        return Response.ok(200);
    }

    /**
     * 内部接口，获取用户的 nickname 和 avatar_url
     */
    @GetMapping("/internal/users/info")
    public R<Recipe.AuthorInfo> getUserInfo(Long userId) {
        UserProfiles user = userProfilesService.getById(userId);
        Recipe.AuthorInfo authorInfo = new Recipe.AuthorInfo();
        authorInfo.setNickname(user.getNickname());
        authorInfo.setAvatarUrl(user.getAvatarUrl());
        return R.ok("获取成功", authorInfo);
    }

}
