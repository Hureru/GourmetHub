package com.hureru.order.mapper;

import com.hureru.order.bean.OrderItems;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 订单中的具体商品项 Mapper 接口
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@Mapper
public interface OrderItemsMapper extends BaseMapper<OrderItems> {

}
