package com.hureru.iam.service;

import com.hureru.iam.bean.Users;
import com.baomidou.mybatisplus.extension.service.IService;

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
}
