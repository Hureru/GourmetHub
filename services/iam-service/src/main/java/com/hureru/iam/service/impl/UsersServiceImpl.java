package com.hureru.iam.service.impl;

import com.hureru.iam.bean.UserProfiles;
import com.hureru.iam.bean.UserRoleMapping;
import com.hureru.iam.bean.Users;
import com.hureru.common.exception.BusinessException;
import com.hureru.iam.mapper.UserProfilesMapper;
import com.hureru.iam.mapper.UserRoleMappingMapper;
import com.hureru.iam.mapper.UsersMapper;
import com.hureru.iam.service.IUsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 存储用户核心认证信息的表 服务实现类
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@Service
@RequiredArgsConstructor
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements IUsersService {
    private final UserProfilesMapper userProfilesMapper;
    private final UserRoleMappingMapper userRoleMappingMapper;

    @Override
    @Transactional
    public Users userRegister(String email, String password, String nickname) {
        // 添加 用户
        Users user = new Users(email, password);
        try {
            save(user);
        } catch (DuplicateKeyException e) {
            // 解析异常信息判断是哪个字段冲突
            if (e.getMessage().contains("email")) {
                throw new BusinessException(409, "邮箱已存在");
            }
            // 其他唯一键冲突
            throw e;
        }
        // 添加 用户信息
        UserProfiles userProfile = new UserProfiles(user.getId(), nickname);
        userProfilesMapper.insert(userProfile);
        // 添加 用户角色映射
        UserRoleMapping userRoleMapping = new UserRoleMapping(user.getId(), 1);
        userRoleMappingMapper.insert(userRoleMapping);
        return user;
    }
}
