package com.hureru.order.controller;


import com.hureru.common.Response;
import com.hureru.common.utils.JwtUtil;
import com.hureru.order.service.ICartItemsService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车中的商品项 前端控制器
 *
 * @author zheng
 * @since 2025-07-26
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CartItemsController {
    private final ICartItemsService cartItemsService;

    /**
     * 添加购物车项、更新购物车项数量 +/-
     *
     * @param productId 商品ID
     * @param quantity  数量
     * @return {@code 200 OK} 购物车项更新成功
     */
    @PatchMapping("/cart-items/{id}")
    @PreAuthorize("hasAuthority('SCOPE_cart-items.update')")
    public Response updateCartItem(@AuthenticationPrincipal Jwt jwt, @PathVariable("id") String productId, @RequestParam Integer quantity) {
        Long userId = JwtUtil.getUserIdFromJwt(jwt);
        boolean result = cartItemsService.updateCartItem(userId, productId, quantity);
        if (!result) {
            return Response.error("更新购物车失败");
        }
        return Response.ok();
    }

    /**
     * 批量添加购物车项
     *
     * @param productIds 商品ID
     * @return {@code 200 OK} 购物车项添加成功
     */
    @PostMapping("/cart-items")
    @PreAuthorize("hasAuthority('SCOPE_cart-items.create')")
    public Response addCartItems(@AuthenticationPrincipal Jwt jwt, @RequestBody String[] productIds) {
        Long userId = JwtUtil.getUserIdFromJwt(jwt);
        boolean result = cartItemsService.batchAddCartItems(userId, List.of(productIds));
        if (!result) {
            return Response.error("添加购物车失败");
        }
        return Response.ok();
    }

    /**
     * 批量删除购物车项
     *
     * @param cartItemId 购物车项ID
     * @return {@code 200 OK} 购物车项删除成功
     */
    @DeleteMapping("/cart-items")
    @PreAuthorize("hasAuthority('SCOPE_cart-items.delete')")
    public Response removeCartItems(@AuthenticationPrincipal Jwt jwt, @RequestBody Long[] cartItemId) {
        Long userId = JwtUtil.getUserIdFromJwt(jwt);
        boolean result = cartItemsService.batchRemoveCartItems(userId, List.of(cartItemId));
        if (!result) {
            return Response.error("删除购物车失败");
        }
        return Response.ok();
    }
}
