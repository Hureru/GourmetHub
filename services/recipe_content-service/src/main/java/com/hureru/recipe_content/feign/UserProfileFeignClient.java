package com.hureru.recipe_content.feign;

import com.hureru.common.R;
import com.hureru.recipe_content.bean.Recipe;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author zheng
 */
// TODO 兜底回调
@FeignClient(value = "iam-service")
public interface UserProfileFeignClient {

    @GetMapping("api/v1/internal/users/info")
    R<Recipe.AuthorInfo> getUserInfo(Long userId);

}
