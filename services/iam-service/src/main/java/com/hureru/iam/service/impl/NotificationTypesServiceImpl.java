package com.hureru.iam.service.impl;

import com.hureru.iam.bean.NotificationTypes;
import com.hureru.iam.mapper.NotificationTypesMapper;
import java.com.hureru.iam.service.INotificationTypesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 通知类型表 服务实现类
 * </p>
 *
 * @author zheng
 * @since 2025-08-07
 */
@Service
public class NotificationTypesServiceImpl extends ServiceImpl<NotificationTypesMapper, NotificationTypes> implements INotificationTypesService {

}
