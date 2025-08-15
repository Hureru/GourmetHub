package com.hureru.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author zheng
 */
@FeignClient(name = "iam-service", path = "/api/v1")
public interface AddressFeignClient {
    @GetMapping("/internal/addresses/{id}")
    String getAddressById(@PathVariable("id") Long addrId);
}
