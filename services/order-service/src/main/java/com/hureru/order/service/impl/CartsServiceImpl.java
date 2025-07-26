package com.hureru.order.service.impl;

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

}
