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
     * 更新购物车项
     * @param userId 用户 id
     * @param productId 商品 id
     * @param quantity 数量 可以为 负
     * @return 添加成功
     */
    boolean updateCartItem(String userId, String productId, Integer quantity);
}
