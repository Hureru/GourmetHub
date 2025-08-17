package com.hureru.order.controller;


import com.hureru.common.PaginationData;
import com.hureru.common.R;
import com.hureru.common.utils.JwtUtil;
import com.hureru.order.dto.CreateOrderDirectlyDTO;
import com.hureru.order.dto.CreateOrderFromCartDTO;
import com.hureru.order.dto.OrderDTO;
import com.hureru.order.service.IOrdersService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
     * 获取当前用户的所有订单
     *
     * @param page 当前页码
     * @param size 每页大小
     * @return 订单列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_order.get')")
    public PaginationData<OrderDTO> getUserOrders(
            @AuthenticationPrincipal Jwt jwt,
            @Min(1) @RequestParam(defaultValue = "1") int page,
            @Min(5) @RequestParam(defaultValue = "10") int size) {
        Long userId = JwtUtil.getUserIdFromJwt(jwt);
        return ordersService.getUserOrders(userId, page, size);
    }

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
    @GetMapping("/{orderSn}")
    @PreAuthorize("hasAuthority('SCOPE_orders.view')")
    public R<OrderDTO> getOrderByOrderSn(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String orderSn) {
        Long userId = JwtUtil.getUserIdFromJwt(jwt);
        OrderDTO order = ordersService.getOrderFromUser(userId, orderSn);
        return R.ok(order);
    }
}
