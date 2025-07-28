package com.hureru.iam.service;

import com.hureru.iam.RoleEnum;
import com.hureru.iam.bean.UserRoleMapping;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户与角色的多对多映射关系表 服务类
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
public interface IUserRoleMappingService extends IService<UserRoleMapping> {
    void updateUserRole(Long userId, Long updateId, RoleEnum role);
}
