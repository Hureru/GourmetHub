package com.hureru.iam;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zheng
 */

@Getter
public enum RoleEnum {
    ROLE_USER(1),
    ROLE_ARTISAN(2),
    ROLE_ADMIN(3),
    ROLE_MODERATOR(4);

    private final int code;

    RoleEnum(int code) {
        this.code = code;
    }

    // 替代ordinal()方法
    public int getIndex() {
        return code;
    }
}

//public enum Role {
//    ROLE_ADMIN,
//    ROLE_USER,
//    ROLE_GUEST;
//
//    // 转换为Spring Security的GrantedAuthority
//    public GrantedAuthority toAuthority() {
//        return new SimpleGrantedAuthority(this.name());
//    }
//}
//
//// 在用户实体中使用
//@Entity
//public class User {
//    @Enumerated(EnumType.STRING)
//    private Role role;
//
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of(role.toAuthority());
//    }
//}