package com.hureru.iam.mapper;

import com.hureru.iam.bean.Addresses;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 存储用户配送地址的表 Mapper 接口
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@Mapper
public interface AddressesMapper extends BaseMapper<Addresses> {

}
