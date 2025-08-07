package com.hureru.product_artisan.controller;

import com.hureru.common.PaginationData;
import com.hureru.common.R;
import com.hureru.product_artisan.bean.Product;
import com.hureru.product_artisan.dto.ProductQueryDTO;
import com.hureru.product_artisan.service.IProductService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 根据动态条件分页搜索产品
     * @param queryDTO 查询条件，通过请求体传入
     * @param page 当前页码 从 1 开始
     * @param size 每页数量 最小为 5
     * @return 分页后的产品数据
     */
    @PostMapping("/public/products") // 使用 POST 更适合复杂查询
    public R<PaginationData<Product>> searchProducts(
            @RequestBody ProductQueryDTO queryDTO, // 从请求体获取查询条件
            @Min(1) @RequestParam(defaultValue = "1") int page,
            @Min(5) @RequestParam(defaultValue = "10") int size) {

        // 创建 Pageable 对象，可以添加默认排序
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        // 调用新的 service 方法
        Page<Product> productPage = productService.searchProducts(queryDTO, pageable, true);

        // 转换为自定义的 PaginationData 对象
        PaginationData<Product> paginationData = new PaginationData<>(
                productPage.getContent(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.getNumber(),
                productPage.getSize()
        );

        log.debug("[controller] searchProducts.....");
        return R.ok("ok", paginationData);
    }


}
