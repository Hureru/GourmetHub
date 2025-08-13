package com.hureru.order.service.impl;

import com.hureru.common.exception.BusinessException;
import com.hureru.order.bean.CartItems;
import com.hureru.order.mapper.CartItemsMapper;
import com.hureru.order.service.ICartItemsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hureru.order.service.ICartsService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CartItemsServiceImpl extends ServiceImpl<CartItemsMapper, CartItems> implements ICartItemsService {
    private final ICartsService cartsService;

    @Override
    public boolean updateCartItem(String userId, String productId, Integer quantity) {
        // 获取购物车 ID（一个用户一个购物车）
        Long cartId = cartsService.getUserCart(userId);

        // 查询是否已有这个商品
        CartItems existItem = this.lambdaQuery()
                .eq(CartItems::getCartId, cartId)
                .eq(CartItems::getProductId, productId)
                .one();

        if (existItem == null) {
            // 没有 → 新增
            if (quantity > 0) {
                CartItems newItem = new CartItems();
                newItem.setCartId(cartId);
                newItem.setProductId(productId);
                newItem.setQuantity(quantity);
                return this.save(newItem);
            } else {
                throw new BusinessException(400, "商品数量不能小于 0");
            }
        } else {
            // 存在 → 更新或删除
            int newQuantity = existItem.getQuantity() + quantity;
            if (newQuantity > 0) {
                return this.lambdaUpdate()
                        .set(CartItems::getQuantity, existItem.getQuantity() + quantity)
                        .eq(CartItems::getId, existItem.getId())
                        .update();
            } else {
                return this.removeById(existItem.getId());
            }
        }
    }
}
