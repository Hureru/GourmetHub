package com.hureru.recipe_content.feign;

import com.hureru.common.R;
import com.hureru.recipe_content.bean.Recipe;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author zheng
 */
@FeignClient(value = "product-artisan-service")
public interface ArtisanFeignClient {
    @GetMapping("/api/v1/internal/artisan/info")
    R<Recipe.AuthorInfo> getAuthorInfo(Long userId);
}
