package com.hureru.iam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hureru.iam.RoleEnum;
import com.hureru.iam.bean.UserProfiles;
import com.hureru.iam.bean.UserRoleMapping;
import com.hureru.iam.bean.Users;
import com.hureru.common.exception.BusinessException;
import com.hureru.iam.mapper.UserProfilesMapper;
import com.hureru.iam.mapper.UserRoleMappingMapper;
import com.hureru.iam.mapper.UsersMapper;
import com.hureru.iam.service.IUsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hureru.product_artisan.dto.ArtisanDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * <p>
 * 存储用户核心认证信息的表 服务实现类
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements IUsersService {
    private final UserProfilesMapper userProfilesMapper;
    private final UserRoleMappingMapper userRoleMappingMapper;
    private final PasswordEncoder passwordEncoder;
    // 注入 RocketMQTemplate
    private final RocketMQTemplate rocketMQTemplate;

    // Topic 定义
    public static final String TOPIC_ARTISAN_CREATE = "TOPIC_ARTISAN_CREATE";

    @Override
    @Transactional
    @CacheEvict(value = "user-details", key = "#email")
    public Users userRegister(String email, String password, String nickname) {
        Users user = register(email, password, true);
        user.setStatus(Users.Status.ACTIVE);
        // 添加 用户信息
        UserProfiles userProfile = new UserProfiles(user.getId(), nickname);
        userProfilesMapper.insert(userProfile);
        // 添加 用户角色映射
        UserRoleMapping userRoleMapping = new UserRoleMapping(user.getId(), RoleEnum.ROLE_USER.getCode());
        userRoleMappingMapper.insert(userRoleMapping);
        return user;
    }

    @Override
    @Transactional
    @CacheEvict(value = "user-details", key = "#artisanDTO.email")
    // MQ 保证数据一致性
    public Users artisanRegister(ArtisanDTO artisanDTO) {
        // 1. 执行本地数据库操作，创建用户和角色
        Users user = register(artisanDTO.getEmail(), artisanDTO.getPassword(), false);
        UserRoleMapping userRoleMapping = new UserRoleMapping(user.getId(), RoleEnum.ROLE_ARTISAN.getCode());
        userRoleMappingMapper.insert(userRoleMapping);

        // 2. 将数据库生成的 ID 填充到 DTO 中
        artisanDTO.setId(String.valueOf(user.getId()));

        // 3. 使用 syncSend 同步发送消息。
        //    因为整个方法被 @Transactional 注解，如果消息发送失败抛出异常，
        //    数据库操作会自动回滚，保证了原子性。
        try {
            rocketMQTemplate.syncSend(TOPIC_ARTISAN_CREATE, artisanDTO);
            log.info("本地事务成功，并已同步发送创建商家消息, User ID: {}", user.getId());
        } catch (Exception e) {
            log.error("同步发送创建商家消息失败, User ID: {}. 事务将回滚.", user.getId(), e);
            // 抛出运行时异常，以触发 @Transactional 的回滚
            throw new BusinessException(500, "消息系统通信失败，请稍后重试", e.getMessage());
        }

        // 4. 返回创建成功的 User 对象
        return user;
    }

    @Override
    public List<String> getPendingUserIds() {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", Users.Status.PENDING_VERIFICATION);
        return list(queryWrapper).stream().map(user -> String.valueOf(user.getId())).toList();
    }

    @Override
    public boolean checkArtisanEffective(String userId) {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", userId);
        queryWrapper.eq("is_artisan", true);
        queryWrapper.eq("status", Users.Status.ACTIVE);
        return count(queryWrapper) > 0;
    }

    @Override
    public void activateArtisan(String userId, Boolean active) {
        UpdateWrapper<Users> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", userId);
        updateWrapper.eq("is_artisan", true);
        updateWrapper.eq("status", Users.Status.PENDING_VERIFICATION);
        if (active){
            updateWrapper.set("status", Users.Status.ACTIVE);
        }else{
            updateWrapper.set("status", Users.Status.SUSPENDED);
        }

        if (!update(updateWrapper)){
            throw new BusinessException(500, "更新失败");
        }
    }

    @Override
    public void updateUserStatus(String userId, Users.Status status) {
        UpdateWrapper<Users> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", userId);
        updateWrapper.set("status", status);
        if (!update(updateWrapper)){
            throw new BusinessException(500, "更新失败");
        }
    }

    private Users register(String email, String password, boolean isUser){
        // 添加 用户
        // 对密码进行加密
        String encodedPassword = passwordEncoder.encode(password);

        Users user = new Users(email, encodedPassword);
        if (isUser){
            user.setStatus(Users.Status.ACTIVE);
        }else {
            user.setIsArtisan(true);
        }
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
        return user;
    }
}
