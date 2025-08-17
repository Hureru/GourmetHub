package com.hureru.order.controller;


import com.hureru.common.PaginationData;
import com.hureru.common.R;
import com.hureru.common.utils.JwtUtil;
import com.hureru.order.bean.CartItems;
import com.hureru.order.service.ICartItemsService;
import com.hureru.order.service.ICartsService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户购物车主表 前端控制器
 *
 * @author zheng
 * @since 2025-07-26
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/carts")
public class CartsController {
    private final ICartItemsService  cartItemsService;

    /**
     * 获取用户购物车
     * @param jwt 用户信息
     * @param page 页码
     * @param size 每页数量
     *
     * @return 用户购物车
     */
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_cart.get')")
    public R<PaginationData<CartItems>> getCart(
            @AuthenticationPrincipal Jwt jwt,
            @Min(1) @RequestParam(defaultValue = "1") int page,
            @Min(5) @RequestParam(defaultValue = "10") int size) {
        Long userId = JwtUtil.getUserIdFromJwt(jwt);
        PaginationData<CartItems> cart = cartItemsService.getUserCart(userId, page, size);
        return R.ok(cart);
    }
}
