package com.hureru.iam.service.impl;

import com.hureru.iam.bean.UserRoleMapping;
import com.hureru.iam.mapper.UserRoleMappingMapper;
import com.hureru.iam.service.IUserRoleMappingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户与角色的多对多映射关系表 服务实现类
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@Service
public class UserRoleMappingServiceImpl extends ServiceImpl<UserRoleMappingMapper, UserRoleMapping> implements IUserRoleMappingService {

}
