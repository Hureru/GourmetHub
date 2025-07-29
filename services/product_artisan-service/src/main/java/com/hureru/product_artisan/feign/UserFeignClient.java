package com.hureru.product_artisan.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author zheng
 */
@FeignClient(value = "iam-service")
public interface UserFeignClient {
    @GetMapping("/api/v1/users/pending")
    List<String> getPendingUsers();

}
