package com.hureru.iam.mapper;

import com.hureru.iam.bean.UserProfiles;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 存储用户个人资料的表 Mapper 接口
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@Mapper
public interface UserProfilesMapper extends BaseMapper<UserProfiles> {

}
