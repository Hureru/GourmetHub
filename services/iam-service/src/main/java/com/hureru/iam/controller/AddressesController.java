package com.hureru.iam.controller;


import com.hureru.common.R;
import com.hureru.iam.bean.Addresses;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 存储用户配送地址的表 前端控制器
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@RestController
@RequestMapping("/api/v1")
public class AddressesController {
    /**
     * 受保护接口, 获取当前用户所有配送地址
     * @param accessToken 访问令牌
     * @return {@code 200 OK}所有配送地址
     */
    @GetMapping("/users/me/addresses")
    public R getAddresses(String accessToken){
        // TODO 实现获取当前用户所有配送地址逻辑
        return R.ok();
    }

    /**
     * 受保护接口, 为当前用户添加新配送地址
     * @param accessToken 访问令牌
     * @param address 配送地址
     * @return {@code 201 Created} 成功放回新增的配送地址
     */
    @PostMapping("/users/me/addresses")
    public R createAddress(@RequestParam String accessToken, @RequestBody Addresses address){
        // TODO 实现创建配送地址逻辑
        return R.ok();
    }
}
