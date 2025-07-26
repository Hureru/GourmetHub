package com.hureru.iam.mapper;

import com.hureru.iam.bean.Users;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 存储用户核心认证信息的表 Mapper 接口
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@Mapper
public interface UsersMapper extends BaseMapper<Users> {

}
