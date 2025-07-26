package com.hureru.iam.mapper;

import com.hureru.iam.bean.UserRoleMapping;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户与角色的多对多映射关系表 Mapper 接口
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@Mapper
public interface UserRoleMappingMapper extends BaseMapper<UserRoleMapping> {

}
