package com.hureru.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hureru.order.bean.Carts;
import com.hureru.order.mapper.CartsMapper;
import com.hureru.order.service.ICartsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户购物车主表 服务实现类
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@Service
public class CartsServiceImpl extends ServiceImpl<CartsMapper, Carts> implements ICartsService {

    @Override
    //TODO 使用 Redis 缓存
    public Long getUserCart(Long userId) {
        Carts cart = getOne(new QueryWrapper<Carts>().eq("user_id", userId));
        if (cart != null) {
            return cart.getId();
        }
        Carts arg = new Carts().setUserId(userId);
        save(arg);
        return arg.getId();
    }
}
