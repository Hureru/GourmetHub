package com.hureru.product_artisan.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author zheng
 */
//TODO 兜底回调
@FeignClient(value = "iam-service")
public interface UserFeignClient {
    @GetMapping("/api/v1/internal/users/pending")
    List<String> getPendingUsers();

    @GetMapping("/api/v1/internal/isEffectiveArtisan")
    Boolean isEffectiveArtisan(@RequestParam String id);

}
