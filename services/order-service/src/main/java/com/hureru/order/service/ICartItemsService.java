package com.hureru.order.service;

import com.hureru.common.PaginationData;
import com.hureru.order.bean.CartItems;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;

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
     * 获取用户购物车
     * @param userId 用户 id
     * @return 购物车 列表
     */
    PaginationData<CartItems> getUserCart(Long userId, int page, int size);

    /**
     * 更新购物车项
     * @param userId 用户 id
     * @param productId 商品 id
     * @param quantity 数量 可以为 负
     * @return 添加成功
     */
    boolean updateCartItem(Long userId, String productId, Integer quantity);

    /**
     * 批量添加购物车项 数量默认为 1
     * @param userId 用户 id
     * @param productIds 商品 id
     * @return 添加成功
     */
    boolean batchAddCartItems(Long userId, Collection<String> productIds);

    /**
     * 批量删除购物车项
     * @param userId 用户 id
     * @param cartItemId 购物车项 id
     * @return 删除成功
     */
    boolean batchRemoveCartItems(Long userId, Collection<Long> cartItemId);
}
