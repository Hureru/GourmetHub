package com.hureru.iam.service;

import com.hureru.iam.bean.Users;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hureru.product_artisan.dto.ArtisanDTO;

import java.util.List;

/**
 * <p>
 * 存储用户核心认证信息的表 服务类
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
public interface IUsersService extends IService<Users> {
    Users userRegister(String email, String password, String nickname);

    Users artisanRegister(ArtisanDTO artisanDTO);
    // 获取待审核的用户ID列表
    List<String> getPendingUserIds();
    // 激活/禁用商家
    void activateArtisan(String userId, Boolean active);
    // 更新用户状态
    void updateUserStatus(String userId, Users.Status status);
}
