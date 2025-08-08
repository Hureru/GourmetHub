package com.hureru.iam.oauth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hureru.iam.bean.Users;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Spring Security 使用的核心用户对象。
 * 它包装了数据库实体 Users，使其适配 UserDetails 接口。
 * @author zheng
 */
public class SecurityUser implements UserDetails {

    /**
     * 原始的数据库用户实体。
     * 使用 @Getter 方便在其他地方（如 JwtTokenCustomizer）直接获取完整的用户信息。
     */
    @Getter
    private final Users user;

    /**
     * 用户的权限集合。
     */
    private final Collection<? extends GrantedAuthority> authorities;

    public SecurityUser(Users user, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }


    @Override
    @JsonIgnore
    public String getPassword() {
        // 从包装的 user 对象中获取密码哈希
        return this.user.getPasswordHash();
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        // 从包装的 user 对象中获取用户名（这里是邮箱）
        return this.user.getEmail();
    }

    // --- 下面是账户状态的方法，根据你的 Users 实体类字段来实现 ---

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        // 假设账户永不过期
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        // 根据你的业务逻辑，当用户状态不是 SUSPENDED 时，账户未被锁定
        return !Users.Status.SUSPENDED.equals(this.user.getStatus());
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        // 假设凭证永不过期
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        // 根据你的业务逻辑，当用户状态是 ACTIVE 时，账户启用
        return Users.Status.ACTIVE.equals(this.user.getStatus());
    }
}
