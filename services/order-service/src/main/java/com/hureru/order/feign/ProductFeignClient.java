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
@FeignClient(name = "product-artisan-service", path = "/api/v1")
public interface ProductFeignClient {

    @GetMapping("/products/{id}")
    R<Product> getProduct(@PathVariable String id);

    @GetMapping("/internal/products/batch")
    R<List<Product>> getProductsByIds(@RequestParam("ids") List<String> ids);

    @GetMapping("/internal/productIds")
    R<List<String>> getProductIdsByArtisanId();
}
