package com.hureru.search.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "product-artisan-service")
public interface ProductFeignClient {
}
