package com.hureru.iam.service;

import com.hureru.iam.bean.UserProfiles;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hureru.iam.dto.UserProfileDTO;


/**
 * <p>
 * 存储用户个人资料的表 服务类
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
public interface IUserProfilesService extends IService<UserProfiles> {
    void updateUserByFields(Long id, UserProfileDTO dto);
}
