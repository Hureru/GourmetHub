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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
    private final RabbitTemplate rabbitTemplate;


    @Override
    @Transactional
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
    // MQ 保证数据一致性
    public Users artisanRegister(ArtisanDTO artisanDTO) {
        // 1. 在本地事务中创建用户和角色映射
        Users user = register(artisanDTO.getEmail(), artisanDTO.getPassword(), false);
        UserRoleMapping userRoleMapping = new UserRoleMapping(user.getId(), RoleEnum.ROLE_ARTISAN.getCode());
        userRoleMappingMapper.insert(userRoleMapping);

        // 2. 准备消息体，将新创建的用户ID设置到DTO中
        artisanDTO.setId(String.valueOf(user.getId()));

        // 3. 注册一个事务同步回调，确保在事务成功提交后才发送消息
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    final String EXCHANGE_NAME = "gourmethub.direct";
                    final String ROUTING_KEY = "routing.artisan.create";
                    rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, artisanDTO);
                    log.info("事务提交成功，已发送创建商家消息到MQ, User ID: {}", user.getId());
                } catch (Exception e) {
                    log.error("发送创建商家消息到MQ失败, User ID: {}. 请手动处理！错误: {}", user.getId(), e.getMessage());
                    //TODO 此处应有补偿机制，例如记录失败日志到数据库，由定时任务重试
                }
            }
        });

        // 4. 立即返回创建的用户信息给调用方
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
