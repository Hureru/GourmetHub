package com.hureru.order.mapper;

import com.hureru.order.bean.CartItems;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 购物车中的商品项 Mapper 接口
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@Mapper
public interface CartItemsMapper extends BaseMapper<CartItems> {

}
