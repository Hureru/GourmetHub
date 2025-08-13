package com.hureru.order.service;

import com.hureru.order.bean.Carts;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户购物车主表 服务类
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
public interface ICartsService extends IService<Carts> {
    /**
     * 获取用户购物车 id
     * @param userId 用户 id
     * @return 购物车 id
     */
    Long getUserCart(String userId);

}
