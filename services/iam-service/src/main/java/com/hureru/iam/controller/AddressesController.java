package com.hureru.iam.controller;


import com.hureru.common.R;
import com.hureru.iam.bean.Addresses;
import com.hureru.iam.dto.AddressDTO;
import com.hureru.iam.dto.group.Create;
import com.hureru.iam.dto.group.Update;
import com.hureru.iam.service.IAddressesService;
import lombok.RequiredArgsConstructor;
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
public class AddressesController {
    private final IAddressesService addressesService;
    /**
     * 受保护接口, 获取当前用户所有配送地址
     *
     * @return {@code 200 OK}所有配送地址
     */
    @GetMapping("/users/me/addresses")
    public R getAddresses(/*@AuthenticationPrincipal Jwt jwt*/){
        // TODO 从JWT中获取用户ID
        Long userId = 1L;
        // 获取当前用户所有配送地址
        List<Addresses> addresses = addressesService.getAllAddressesByUserId(userId);
        return R.ok("获取用户地址成功", addresses);
    }

    /**
     * 受保护接口, 为当前用户添加新配送地址
     * @param address 配送地址
     * @return {@code 201 Created} 成功放回新增的配送地址
     */
    @PostMapping("/users/me/addresses")
    public R createAddress(/*@AuthenticationPrincipal Jwt jwt,*/ @RequestBody Addresses address){
        // TODO 实现创建配送地址逻辑
        return R.ok();
    }
}
