package com.hureru.order.mapper;

import com.hureru.order.bean.Carts;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户购物车主表 Mapper 接口
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@Mapper
public interface CartsMapper extends BaseMapper<Carts> {

}
