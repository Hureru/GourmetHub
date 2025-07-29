package com.hureru.iam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hureru.common.exception.BusinessException;
import com.hureru.iam.bean.Addresses;
import com.hureru.iam.dto.AddressDTO;
import com.hureru.iam.mapper.AddressesMapper;
import com.hureru.iam.service.IAddressesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.PropertyDescriptor;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.baomidou.mybatisplus.core.toolkit.StringUtils.camelToUnderline;

/**
 * <p>
 * 存储用户配送地址的表 服务实现类
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@Service
public class AddressesServiceImpl extends ServiceImpl<AddressesMapper, Addresses> implements IAddressesService {

    @Override
    public List<Addresses> getAllAddressesByUserId(Long userId){
        QueryWrapper<Addresses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return list(queryWrapper);
    }

    @Override
    @Transactional
    public Addresses insertAddress(Long userId, AddressDTO addressDTO){
        // 判断用户地址数量是否超过上限
        QueryWrapper<Addresses> countWrapper = new QueryWrapper<>();
        countWrapper.eq("user_id", userId);
        long addressCount = count(countWrapper);
        if (addressCount >= 10) {
            throw new BusinessException(400, "地址数量已达上限");
        }

        Addresses address = new Addresses();
        address.setUserId(userId);
        address.setRecipientName(addressDTO.getRecipientName());
        address.setPhoneNumber(addressDTO.getPhoneNumber());
        address.setStreetAddress(addressDTO.getStreetAddress());
        address.setCity(addressDTO.getCity());
        address.setStateProvince(addressDTO.getStateProvince());
        address.setIsDefault(false);
        address.setCreatedAt(LocalDateTime.now());
        address.setUpdatedAt(LocalDateTime.now());
        save(address);
        return address;
    }

    // 预缓存字段描述符（避免每次反射）
    private static final List<PropertyDescriptor> DTO_FIELDS =
            Arrays.stream(BeanUtils.getPropertyDescriptors(AddressDTO.class))
                    .filter(pd -> !"class".equals(pd.getName()))
                    .toList();
    @Override
    @Transactional
    public void updateAddress(Long addrId, Long userId, AddressDTO addresses){
        // 验证地址所属用户
        checkUserAddr(addrId, userId);
        UpdateWrapper<Addresses> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", addrId);

        // 使用缓存字段描述符
        DTO_FIELDS.forEach(pd -> {
            try {
                Object value = pd.getReadMethod().invoke(addresses);
                if (value != null) {
                    updateWrapper.set(camelToUnderline(pd.getName()), value);
                }
            } catch (Exception ignored) {}
        });

        if(!update(updateWrapper)){
            // "配送地址更新失败"
            throw new BusinessException(500, "配送地址更新失败");
        }
    }

    @Override
    @Transactional
    public void updateDefaultAddr(Long addrId, Long userId) {
        // 验证地址所属用户
        checkUserAddr(addrId, userId);
        UpdateWrapper<Addresses> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId);
        updateWrapper.set("is_default", false);
        if(!update(null, updateWrapper)){
            throw new BusinessException(500, "更新失败");
        }

        updateWrapper.clear();
        updateWrapper.eq("id", addrId);
        updateWrapper.set("is_default", true);
        if (!update(null, updateWrapper)){
            throw new BusinessException(500, "更新失败");
        }
    }

    @Override
    @Transactional
    public void deleteAddress(Long addrId, Long userId) {
        // 验证地址所属用户
        checkUserAddr(addrId, userId);

        if (!removeById(addrId)){
            // "删除配送地址失败"
            throw new BusinessException(500, "删除配送地址失败");
        }
    }


    private void checkUserAddr(Long addrId, Long userId) {
        Addresses existingAddress = getById(addrId);
        if (existingAddress == null) {
            // "配送地址不存在"
            throw new BusinessException(404, "配送地址不存在");
        }
        if (!existingAddress.getUserId().equals(userId)) {
            // "无权操作该配送地址"
            throw new BusinessException(403, "无权操作该配送地址");
        }
    }

}
