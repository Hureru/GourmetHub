package com.hureru.iam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hureru.iam.RoleEnum;
import com.hureru.iam.bean.Roles;
import com.hureru.iam.bean.UserProfiles;
import com.hureru.iam.bean.UserRoleMapping;
import com.hureru.iam.bean.Users;
import com.hureru.common.exception.BusinessException;
import com.hureru.iam.feign.ArtisanFeignClient;
import com.hureru.iam.mapper.RolesMapper;
import com.hureru.iam.mapper.UserProfilesMapper;
import com.hureru.iam.mapper.UserRoleMappingMapper;
import com.hureru.iam.mapper.UsersMapper;
import com.hureru.iam.service.IUsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hureru.product_artisan.bean.Artisan;
import com.hureru.product_artisan.dto.ArtisanDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements IUsersService, UserDetailsService {
    private final UserProfilesMapper userProfilesMapper;
    private final UserRoleMappingMapper userRoleMappingMapper;
    private final RolesMapper rolesMapper;
    private final PasswordEncoder passwordEncoder;
    private final ArtisanFeignClient artisanFeignClient;

    /**
     * 根据用户名加载用户信息，供 Spring Security 调用。
     * @param email 用户账号
     * @return UserDetails 包含用户信息和权限
     * @throws UsernameNotFoundException 如果用户不存在
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. 根据用户名查询用户
        Users user = getOne(new QueryWrapper<Users>().eq("email", email));
        if (user == null) {
            log.error("查询账号失败: " + email);
            throw new UsernameNotFoundException(String.format("账号 %s 不存在", email));
        }

        // 2. 查询用户的角色映射
        List<UserRoleMapping> roleMappings = userRoleMappingMapper.selectList(new QueryWrapper<UserRoleMapping>().eq("user_id", user.getId()));
        if (roleMappings.isEmpty()) {
            // 如果用户没有角色，返回一个没有权限的 User 对象
            return new User(user.getEmail(), user.getPasswordHash(), Collections.emptyList());
        }

        // 3. 根据角色ID查询角色信息
        List<Integer> roleIds = roleMappings.stream().map(UserRoleMapping::getRoleId).collect(Collectors.toList());
        List<Roles> roles = rolesMapper.selectBatchIds(roleIds);

        // 4. 构建权限列表 (Spring Security 需要 'ROLE_' 前缀)
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().toUpperCase()))
                .collect(Collectors.toList());

        boolean enabled = Users.Status.ACTIVE.equals(user.getStatus());
        boolean accountNonLocked = !Users.Status.SUSPENDED.equals(user.getStatus());

        // 5. 返回 Spring Security 的 User 对象
        return new User(
                user.getEmail(),
                user.getPasswordHash(),
                enabled,
                true,
                true,
                accountNonLocked,
                authorities);
    }

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
    //TODO OpenFeign 远程调用全局事务
    public Users artisanRegister(ArtisanDTO artisanDTO) {
        Users user = register(artisanDTO.getEmail(), artisanDTO.getPassword(), false);
        // 添加 商家角色映射
        UserRoleMapping userRoleMapping = new UserRoleMapping(user.getId(), RoleEnum.ROLE_ARTISAN.getCode());
        userRoleMappingMapper.insert(userRoleMapping);
        // 调用 product_artisan-service 服务 注册商家信息
        artisanDTO.setId(String.valueOf(user.getId()));
        Artisan artisan = artisanFeignClient.addArtisan(artisanDTO);
        log.info("dto: {}", artisanDTO);
        log.info("调用OpenFeign[artisanFeignClient.addArtisan]:{}", artisan);
        return user;
    }

    @Override
    public List<String> getPendingUserIds() {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", Users.Status.PENDING_VERIFICATION);
        return list(queryWrapper).stream().map(user -> String.valueOf(user.getId())).toList();
    }

    @Override
    public void activateArtisan(String userId, Boolean active) {
        UpdateWrapper<Users> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", userId);
        updateWrapper.eq("is_artisan", true);
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
