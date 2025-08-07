package com.hureru.iam.service.impl;

import com.hureru.iam.bean.UserNotificationPreferences;
import com.hureru.iam.mapper.UserNotificationPreferencesMapper;
import java.com.hureru.iam.service.IUserNotificationPreferencesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户通知偏好设置表 服务实现类
 * </p>
 *
 * @author zheng
 * @since 2025-08-07
 */
@Service
public class UserNotificationPreferencesServiceImpl extends ServiceImpl<UserNotificationPreferencesMapper, UserNotificationPreferences> implements IUserNotificationPreferencesService {

}
