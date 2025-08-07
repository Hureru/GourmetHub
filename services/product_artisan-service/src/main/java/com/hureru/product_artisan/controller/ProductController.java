package com.hureru.product_artisan.controller;

import com.hureru.common.R;
import com.hureru.product_artisan.bean.Product;
import com.hureru.product_artisan.service.IProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zheng
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProductController {
    private final IProductService productService;

    /**
     * 获取已发布产品
     *
     * @return 所有已发布的产品
     */
    @GetMapping("/public/products")
    public R<List<Product>> getPublishedProducts() {
        log.debug("[controller] getPublishedProducts.....");
        List<Product> products = productService.getPublishedProducts();
        return R.ok("success", products);
    }


}
