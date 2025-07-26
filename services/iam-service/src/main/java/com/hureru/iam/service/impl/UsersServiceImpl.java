package com.hureru.iam.service.impl;

import com.hureru.iam.bean.Users;
import com.hureru.iam.mapper.UsersMapper;
import com.hureru.iam.service.IUsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 存储用户核心认证信息的表 服务实现类
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements IUsersService {

}
