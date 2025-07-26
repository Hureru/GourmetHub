package com.hureru.order.service.impl;

import com.hureru.order.bean.CartItems;
import com.hureru.order.mapper.CartItemsMapper;
import com.hureru.order.service.ICartItemsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 购物车中的商品项 服务实现类
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@Service
public class CartItemsServiceImpl extends ServiceImpl<CartItemsMapper, CartItems> implements ICartItemsService {

}
