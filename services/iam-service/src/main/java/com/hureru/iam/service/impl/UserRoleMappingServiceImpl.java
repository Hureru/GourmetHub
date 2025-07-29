package com.hureru.iam.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hureru.common.exception.BusinessException;
import com.hureru.iam.RoleEnum;
import com.hureru.iam.bean.UserRoleMapping;
import com.hureru.iam.mapper.UserRoleMappingMapper;
import com.hureru.iam.service.IUserRoleMappingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户与角色的多对多映射关系表 服务实现类
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@Service
public class UserRoleMappingServiceImpl extends ServiceImpl<UserRoleMappingMapper, UserRoleMapping> implements IUserRoleMappingService {

    @Override
    public void updateUserRole(Long userId, Long updateId, RoleEnum role) {
        // 验证管理员
        int roleId = getById(userId).getRoleId();
        if (RoleEnum.ROLE_ADMIN.getCode() != roleId){
            throw new BusinessException(403, "无权操作");
        }

        if (!update(new UpdateWrapper<UserRoleMapping>().eq("user_id", updateId).set("role_id", role.getCode()))){
            throw new BusinessException(500, "更新失败");
        }
    }
}
