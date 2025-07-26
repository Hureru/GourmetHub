package com.hureru.iam.mapper;

import com.hureru.iam.bean.Roles;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 存储系统所有可用角色的表 Mapper 接口
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@Mapper
public interface RolesMapper extends BaseMapper<Roles> {

}
