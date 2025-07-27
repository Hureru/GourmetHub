package com.hureru.iam.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hureru.iam.bean.UserProfiles;
import com.hureru.iam.mapper.UserProfilesMapper;
import com.hureru.iam.service.IUserProfilesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    @Override
    public UserProfiles updateUserByFields(Long id, Map<String, Object> fields) {
        if (fields == null || fields.isEmpty()) {
            throw new IllegalArgumentException("更新字段不能为空");
        }

        UpdateWrapper<UserProfiles> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", id);

        // 只设置合法字段（白名单方式防止SQL注入）
        List<String> allowedFields = Arrays.asList("nickname", "avatarUrl", "bio");

        fields.forEach((key, value) -> {
            if (allowedFields.contains(key)) {
                String column = camelToUnderline(key); // 驼峰命名转换
                updateWrapper.set(column, value);
            }
        });

        update(null, updateWrapper);
        return getById(id);
    }
}
