package com.hureru.order.feign;

import com.hureru.common.R;
import com.hureru.product_artisan.bean.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author zheng
 */
@FeignClient(name = "product-artisan-service", path = "/api/v1/products")
public interface ProductFeignClient {

    @GetMapping("/{id}")
    R<Product> getProduct(@PathVariable String id);

    @GetMapping("/batch")
    R<List<Product>> getProductsByIds(@RequestParam("ids") List<String> ids);
}
