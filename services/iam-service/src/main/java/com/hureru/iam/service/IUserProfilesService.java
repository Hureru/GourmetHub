package com.hureru.iam.service;

import com.hureru.iam.bean.UserProfiles;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 存储用户个人资料的表 服务类
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
public interface IUserProfilesService extends IService<UserProfiles> {
    UserProfiles updateUserByFields(Long id, Map<String, Object> fields);
}
