package com.hureru.search.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "recipe-content-service")
public interface RecipeFeignClient {
}
