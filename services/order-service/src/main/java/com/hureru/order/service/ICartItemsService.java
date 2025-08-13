package com.hureru.order.service;

import com.hureru.order.bean.CartItems;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 购物车中的商品项 服务类
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
public interface ICartItemsService extends IService<CartItems> {
    /**
     * 添加购物车项
     * @param cartItems 购物车项
     * @return 添加成功
     */
    boolean addCartItem(String cartId, String productId, Integer quantity);
}
