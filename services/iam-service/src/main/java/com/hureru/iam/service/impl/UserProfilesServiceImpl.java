package com.hureru.iam.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hureru.common.exception.BusinessException;
import com.hureru.iam.bean.UserProfiles;
import com.hureru.iam.dto.UserProfileDTO;
import com.hureru.iam.mapper.UserProfilesMapper;
import com.hureru.iam.service.IUserProfilesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;

import static com.baomidou.mybatisplus.core.toolkit.StringUtils.camelToUnderline;

/**
 * <p>
 * 存储用户个人资料的表 服务实现类
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@Service
public class UserProfilesServiceImpl extends ServiceImpl<UserProfilesMapper, UserProfiles> implements IUserProfilesService {

    // 预缓存字段描述符（避免每次反射）
    private static final List<PropertyDescriptor> DTO_FIELDS =
            Arrays.stream(BeanUtils.getPropertyDescriptors(UserProfileDTO.class))
                    .filter(pd -> !"class".equals(pd.getName()))
                    .toList();

    @Override
    public void updateUserByFields(Long id, UserProfileDTO dto) {
        UpdateWrapper<UserProfiles> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", id);

        // 使用缓存字段描述符
        DTO_FIELDS.forEach(pd -> {
            try {
                Object value = pd.getReadMethod().invoke(dto);
                if (value != null) {
                    updateWrapper.set(camelToUnderline(pd.getName()), value);
                }
            } catch (Exception ignored) {}
        });

        if(!update(null, updateWrapper)){
            // "用户不存在"
            throw new BusinessException(404, "用户不存在");
        }
    }
}
