package com.hureru.iam.controller;


import com.hureru.common.R;
import com.hureru.iam.service.IUserProfilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 * 存储用户个人资料的表 前端控制器
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@RestController
@RequestMapping("/api/v1")
public class UserProfilesController {

    @Autowired
    private IUserProfilesService userProfilesService;
    /**
     * 受保护接口, 获取当前认证用户的完整个人资料
     * @param accessToken 有效的Bearer令牌
     * @return {@code 200 OK} 返回个人资料
     * {@code 401 Unauthorized} 令牌无效或缺失
     */
    @GetMapping("/users/me")
    public R me(String accessToken) {
        // TODO 实现获取个人资料逻辑
        return R.ok();
    }

    /**
     * 受保护接口, 更新当前认证用户的个人资料
     * @param accessToken 有效的Bearer令牌
     * @param fields 需要更新的字段列表
     *               <code>nickname/avatarUrl/bio</code>
     * @return {@code 200 OK} 返回更新后的个人资料
     * {@code 401 Unauthorized} 令牌无效或缺失
     */
    @PutMapping("/users/me")
    public R updateMe(Jwt jwt, String accessToken, Map<String, Object> fields) {
        // TODO 实现更新个人资料逻辑
//        Long id = JwtUtils.getUserId(accessToken);
//        return userProfilesService.updateUserByFields(id, fields);
        return null;
    }

}
