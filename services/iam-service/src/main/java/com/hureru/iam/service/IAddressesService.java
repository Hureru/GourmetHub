package com.hureru.iam.service;

import com.hureru.iam.bean.Addresses;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hureru.iam.dto.AddressDTO;

import java.util.List;

/**
 * <p>
 * 存储用户配送地址的表 服务类
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
public interface IAddressesService extends IService<Addresses> {
    List<Addresses> getAllAddressesByUserId(Long userId);

}
