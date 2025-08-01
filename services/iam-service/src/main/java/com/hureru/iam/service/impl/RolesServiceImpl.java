package com.hureru.iam.service.impl;

import com.hureru.iam.bean.Roles;
import com.hureru.iam.mapper.RolesMapper;
import com.hureru.iam.service.IRolesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 存储系统所有可用角色的表 服务实现类
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@Service
public class RolesServiceImpl extends ServiceImpl<RolesMapper, Roles> implements IRolesService {

}
