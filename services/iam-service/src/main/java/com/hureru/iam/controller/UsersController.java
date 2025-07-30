package com.hureru.iam.controller;


import com.hureru.common.R;
import com.hureru.iam.bean.Users;
import com.hureru.iam.dto.UserDTO;
import com.hureru.iam.service.IUsersService;
import com.hureru.iam.dto.group.Create;
import com.hureru.product_artisan.dto.ArtisanDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * {@code 400 Bad Request} 输入参数验证失败
     * {@code 409 Conflict} 邮箱已存在
     */
    @PostMapping("/register/users")
    public R register(@Valid @RequestBody UserDTO userDTO) {
        // 用户注册
        Users user = usersService.userRegister(userDTO.getEmail(), userDTO.getPassword(), userDTO.getNickname());
        log.info("用户注册：{}", user);
        return R.ok(201, "用户注册成功", user);
    }

    /**
     * 公开接口，用于创建新商家账户
     * @param artisanDTO 商家注册信息
     * @return {@code 201 Created} 成功响应示例：
     * <pre>
     * {
     *   "id": "12345",
     *   "email": "new.user@example.com",
     *   "nickname": "GourmetNewbie",
     *   "createdAt": "2023-10-27T10:30:00.123Z"
     * }
     * </pre>
     * {@code 400 Bad Request} 输入参数验证失败
     * {@code 409 Conflict} 邮箱已存在
     */
    @PostMapping("/register/artisan")
    public R registerArtisan(@Validated(Create.class) @RequestBody ArtisanDTO artisanDTO) {
        log.info("[controller] 商家注册：{}", artisanDTO);
        // 商家注册
        Users user = usersService.artisanRegister(artisanDTO);
        return R.ok(201, "商家注册成功", user);
    }

    /**
     * 受保护接口，仅限product_artisan-service 服务调用，获取待审核用户列表
     *
     * @return 待审核用户ID列表
     */
    // TODO GetaWay 保护
    @GetMapping("/internal/users/pending")
    public List<String> getPendingUsers() {
        return usersService.getPendingUserIds();
    }

    /**
     * 激活/禁用 (待审核商家)
     *
     * @param id 用户ID
     * @return 激活结果
     */
    @PreAuthorize("hasAuthority('SCOPE_artisan.active')")
    @PatchMapping("/artisan/{id}/active")
    public R activeUser(@PathVariable String id, @RequestParam Boolean active) {
        usersService.activateArtisan(id, active);
        return R.ok();
    }

    /**
     * 更新用户状态
     *
     * @param id 用户ID
     * @return 更新结果
     */
    @PreAuthorize("hasAuthority('SCOPE_users.status.update')")
    @PatchMapping("/users/{id}/status")
    public R updateUserStatus(@PathVariable String id, @RequestParam Integer status) {
        log.info("status：{}", status);
        Users.Status statusEnum = Users.Status.values()[status];
        log.info("更新状态为：{}", statusEnum);
        usersService.updateUserStatus(id, statusEnum);
        return R.ok();
    }

    @PreAuthorize("hasAuthority('SCOPE_users.delete')")
    @DeleteMapping("/users/{id}")
    public R deleteUser(@PathVariable String id) {
        return usersService.removeById(id) ? R.ok() : R.error("删除用户失败");
    }
}
