package com.hureru.iam.feign;

import com.hureru.product_artisan.bean.Artisan;
import com.hureru.product_artisan.dto.ArtisanDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 商家 OpenFeign 客户端
 * @author zheng
 */
@FeignClient(value = "product-artisan-service")
public interface ArtisanFeignClient {

    @PostMapping("/api/v1/artisan")
    Artisan addArtisan(@RequestBody @Valid ArtisanDTO artisanDTO);
}
