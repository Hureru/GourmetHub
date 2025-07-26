package com.hureru.iam.service.impl;

import com.hureru.iam.bean.UserProfiles;
import com.hureru.iam.mapper.UserProfilesMapper;
import com.hureru.iam.service.IUserProfilesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 存储用户个人资料的表 服务实现类
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@Service
public class UserProfilesServiceImpl extends ServiceImpl<UserProfilesMapper, UserProfiles> implements IUserProfilesService {

}
