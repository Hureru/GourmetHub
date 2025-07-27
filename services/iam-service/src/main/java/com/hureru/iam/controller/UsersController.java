package com.hureru.iam.controller;


import com.hureru.common.R;
import com.hureru.iam.bean.Users;
import com.hureru.iam.dto.UserDTO;
import com.hureru.iam.service.IUsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 存储用户核心认证信息的表 前端控制器
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UsersController {
    private final IUsersService usersService;

    /**
     * 公开接口，用于创建新用户账户
     * @param userDTO 用户注册信息
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
    public R register(@Valid @RequestBody UserDTO userDTO) {
        // 用户注册
        Users user = usersService.userRegister(userDTO.getEmail(), userDTO.getPassword(), userDTO.getNickname());
        userDTO.setUserId(user.getId());
        log.info("用户注册：{}", userDTO);
        return R.ok(201, "用户注册成功", userDTO);
    }

}
