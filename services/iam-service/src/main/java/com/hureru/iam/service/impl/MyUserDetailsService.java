package com.hureru.iam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hureru.iam.bean.Roles;
import com.hureru.iam.bean.UserRoleMapping;
import com.hureru.iam.bean.Users;
import com.hureru.iam.mapper.RolesMapper;
import com.hureru.iam.mapper.UserRoleMappingMapper;
import com.hureru.iam.mapper.UsersMapper;
import com.hureru.iam.oauth.SecurityUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zheng
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    private final UsersMapper usersMapper;
    private final UserRoleMappingMapper userRoleMappingMapper;
    private final RolesMapper rolesMapper;

    /**
     * 根据用户名加载用户信息，供 Spring Security 调用。
     * @param email 用户账号
     * @return UserDetails 包含用户信息和权限
     * @throws UsernameNotFoundException 如果用户不存在
     */
    @Override
    @Cacheable(value = "user-details", key = "#email")
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. 根据用户名查询用户
        // 注意：这里假设你的 UserDetailsService 继承或注入了 IUsersService 的能力
        Users user = usersMapper.selectOne(new QueryWrapper<Users>().eq("email", email));
        if (user == null) {
            log.error("查询账号失败: {}", email);
            throw new UsernameNotFoundException(String.format("账号 %s 不存在", email));
        }

        // 2. 查询用户的角色映射
        List<UserRoleMapping> roleMappings = userRoleMappingMapper.selectList(new QueryWrapper<UserRoleMapping>().eq("user_id", user.getId()));
        if (roleMappings.isEmpty()) {
            log.warn("用户 {} 没有任何角色。", email);
            // 如果用户没有角色，返回一个没有权限的 SecurityUser 对象
            return new SecurityUser(user, Collections.emptyList());
        }

        // 3. 根据角色ID查询角色信息
        List<Integer> roleIds = roleMappings.stream().map(UserRoleMapping::getRoleId).collect(Collectors.toList());
        List<Roles> roles = rolesMapper.selectBatchIds(roleIds);

        // 4. 构建权限列表
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().toUpperCase()))
                .collect(Collectors.toList());

        // 5. 关键改动：返回我们自定义的 SecurityUser 对象
        // 它包装了完整的 Users 实体和权限列表
        return new SecurityUser(user, authorities);
    }
}
