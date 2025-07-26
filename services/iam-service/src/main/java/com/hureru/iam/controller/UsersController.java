package com.hureru.iam.controller;


import com.hureru.common.R;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 存储用户核心认证信息的表 前端控制器
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@RestController
@RequestMapping("/api/v1")
public class UsersController {

    /**
     * 公开接口，用于创建新用户账户
     * @param email 用户邮箱
     * @param password 用户密码
     * @param nickname 用户昵称
     * @return {@code 201 Created} 成功响应示例：
     * <pre>
     * {
     *   "id": "12345",
     *   "email": "new.user@example.com",
     *   "nickname": "GourmetNewbie",
     *   "createdAt": "2023-10-27T10:30:00.123Z"
     * }
     * </pre>
     * {@code 400 Bad Request} 输入验证失败
     * {@code 409 Conflict} 邮箱已存在
     */
    @PostMapping("/users/register")
    public R register(@RequestParam String email,
                      @RequestParam String password,
                      @RequestParam String nickname) {
        // TODO 用户注册
        return R.ok("register", null);
    }
}
