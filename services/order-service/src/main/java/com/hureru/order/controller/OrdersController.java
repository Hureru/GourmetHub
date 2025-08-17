package com.hureru.order.controller;


import com.hureru.common.R;
import com.hureru.common.utils.JwtUtil;
import com.hureru.order.bean.Orders;
import com.hureru.order.dto.CreateOrderDirectlyDTO;
import com.hureru.order.dto.CreateOrderFromCartDTO;
import com.hureru.order.service.IOrdersService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 订单主表 前端控制器
 *
 * @author zheng
 * @since 2025-07-26
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrdersController {
    private final IOrdersService ordersService;

    /**
     * 从购物车创建订单 (已更新为按选中项结算)
     */
    @PostMapping("/from-cart")
    @PreAuthorize("hasAuthority('SCOPE_orders.create')")
    public R<String> createOrderFromCart(
            @AuthenticationPrincipal Jwt jwt,
            @Validated @RequestBody CreateOrderFromCartDTO dto) {
        Long userId = JwtUtil.getUserIdFromJwt(jwt);
        String orderId = ordersService.createOrderFromCart(userId, dto);
        return R.ok("订单创建中，请稍后查看状态", orderId);
    }

    /**
     * 直接购买创建订单
     */
    @PostMapping("/directly")
    @PreAuthorize("hasAuthority('SCOPE_orders.create')")
    public R<String> createOrderDirectly(
            @AuthenticationPrincipal Jwt jwt,
            @Validated @RequestBody CreateOrderDirectlyDTO dto) {
        Long userId = JwtUtil.getUserIdFromJwt(jwt);
        String orderId = ordersService.createOrderDirectly(userId, dto);
        return R.ok("订单创建中，请稍后查看状态", orderId);
    }

    /**
     * 根据业务订单ID查询订单 需要用户权限
     */
    @GetMapping("/{orderId}")
    @PreAuthorize("hasAuthority('SCOPE_orders.view')")
    public R<Orders> getOrderByOrderId(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String orderId) {
        Long userId = JwtUtil.getUserIdFromJwt(jwt);
        Orders order = ordersService.getOrderFromUser(userId, orderId);
        return R.ok(order);
    }
}
