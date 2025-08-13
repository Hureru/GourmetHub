package com.hureru.order.service.impl;

import com.hureru.common.exception.BusinessException;
import com.hureru.order.bean.CartItems;
import com.hureru.order.mapper.CartItemsMapper;
import com.hureru.order.service.ICartItemsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hureru.order.service.ICartsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Transactional
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

    @Override
    @Transactional
    public boolean batchAddCartItems(String userId, Collection<String> productIds) {
        Long cartId = cartsService.getUserCart(userId);
        if (productIds == null || productIds.isEmpty()) {
            return false;
        }

        // 1. 查询已有的这些商品
        List<CartItems> existItems = this.lambdaQuery()
                .eq(CartItems::getCartId, cartId)
                .in(CartItems::getProductId, productIds)
                .list();

        Map<String, CartItems> existMap = existItems.stream()
                .collect(Collectors.toMap(CartItems::getProductId, item -> item));

        List<CartItems> toInsert = new ArrayList<>();
        List<CartItems> toUpdate = new ArrayList<>();

        // 2. 遍历要添加的商品
        for (String entry : productIds) {
            CartItems existItem = existMap.get(entry);
            if (existItem == null) {
                // 不存在 → 新增
                CartItems newItem = new CartItems();
                newItem.setCartId(cartId);
                newItem.setProductId(entry);
                newItem.setQuantity(1);
                toInsert.add(newItem);
            } else {
                // 存在 → 数量累加
                existItem.setQuantity(existItem.getQuantity() + 1);
                toUpdate.add(existItem);
            }
        }

        // 3. 批量执行
        if (!toInsert.isEmpty()) {
            this.saveBatch(toInsert);
        }
        if (!toUpdate.isEmpty()) {
            this.updateBatchById(toUpdate);
        }

        return true;
    }


    @Override
    @Transactional
    public boolean batchRemoveCartItems(String userId, Collection<Long> productIds) {
        Long cartId = cartsService.getUserCart(userId);
        if (productIds == null || productIds.isEmpty()) {
            return false;
        }

        return this.lambdaUpdate()
                .eq(CartItems::getCartId, cartId)
                .in(CartItems::getProductId, productIds)
                .remove();
    }
}
