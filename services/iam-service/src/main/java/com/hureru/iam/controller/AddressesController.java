package com.hureru.iam.controller;


import com.hureru.common.R;
import com.hureru.common.utils.JwtUtil;
import com.hureru.iam.bean.Addresses;
import com.hureru.iam.dto.AddressDTO;
import com.hureru.iam.dto.group.Create;
import com.hureru.iam.dto.group.Update;
import com.hureru.iam.service.IAddressesService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * <p>
 * 存储用户配送地址的表 前端控制器
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@PreAuthorize("hasAuthority('SCOPE_address')")
public class AddressesController {
    private final IAddressesService addressesService;
    /**
     * 受保护接口, 获取当前用户所有配送地址
     * @param jwt 通过 @AuthenticationPrincipal 注解自动注入，包含了当前用户的认证信息
     * @return {@code 200 OK}所有配送地址
     */
    @GetMapping("/users/me/addresses")
    public R getAddresses(@AuthenticationPrincipal Jwt jwt){
        // 从JWT中获取用户ID
        Long userId = JwtUtil.getUserIdFromJwt(jwt);
        // 获取当前用户所有配送地址
        List<Addresses> addresses = addressesService.getAllAddressesByUserId(userId);
        return R.ok("获取用户地址成功", addresses);
    }

    /**
     * 受保护接口, 为当前用户添加新配送地址, 具有上限：10
     * @param jwt 用户令牌
     * @param address 配送地址
     * @return {@code 201 Created} 成功返回新增的配送地址
     */
    @PostMapping("/users/me/addresses")
    public R createAddress(@AuthenticationPrincipal Jwt jwt, @Validated(Create.class) @RequestBody AddressDTO address){
        // 从JWT中获取用户ID
        Long userId = JwtUtil.getUserIdFromJwt(jwt);
        // 创建配送地址
        Addresses insertAddress = addressesService.insertAddress(userId, address);
        return R.ok(201, "创建地址成功", insertAddress);
    }

    /**
     * 受保护接口, 更新配送地址
     * @param jwt 用户令牌
     * @param address 配送地址更新信息
     * @return {@code 200 OK} 成功返回 success
     */
    @PatchMapping("/users/me/addresses/{id}")
    public R updateAddress(@AuthenticationPrincipal Jwt jwt, @PathVariable("id") Long addrId, @Validated(Update.class) @RequestBody AddressDTO address){
        // 从JWT中获取用户ID
        Long userId = JwtUtil.getUserIdFromJwt(jwt);
        // 更新配送地址
        addressesService.updateAddress(addrId, userId, address);
        return R.ok(200);
    }

    /**
     * 设置默认地址
     * @param jwt 用户令牌
     * @param addrId 配送地址ID
     * @return {@code 200 OK}
     */
    @PatchMapping("/users/me/addresses/{id}/default")
    public R updateDefaultAddr(@AuthenticationPrincipal Jwt jwt, @PathVariable("id")Long addrId){
        // 从JWT中获取用户ID
        Long userId = JwtUtil.getUserIdFromJwt(jwt);
        addressesService.updateDefaultAddr(addrId, userId);
        return R.ok(200);
    }

    /**
     * 删除配送地址
     * @param jwt 用户令牌
     * @return {@code 200 OK}
     */
    @DeleteMapping("/users/me/addresses/{id}")
    public R deleteAddress(@AuthenticationPrincipal Jwt jwt, @PathVariable("id") Long addrId) {
        // 从JWT中获取用户ID
        Long userId = JwtUtil.getUserIdFromJwt(jwt);
        // 删除配送地址
        addressesService.deleteAddress(addrId, userId);
        return R.ok(200);
    }



}
