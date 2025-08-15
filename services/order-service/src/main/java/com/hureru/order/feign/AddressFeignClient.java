package com.hureru.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author zheng
 */
@FeignClient(name = "iam-service", path = "/api/v1/users/me/addresses")
public interface AddressFeignClient {
    @GetMapping("/{id}")
    String getAddressById(@PathVariable("id") Long addrId);
}
